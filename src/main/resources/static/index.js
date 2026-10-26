"use strict";
const API = "/api/study-records";
// nullなら新規登録、値があれば編集中
let editingId = null;
// 削除対象のID
let deletingId = null;
let currentPage = 0;
const PAGE_SIZE = 10;
// 表示中の記録をIDで引けるようにする
let recordsById = new Map();
let searchSubject = "";
let searchDate = "";
document.addEventListener("keydown", e => {
    if (e.key === "Escape") closeDeleteModal();
});
// Enterキーでも検索できるようにする
document.getElementById("searchSubject").addEventListener("keydown", e => {
    if (e.key === "Enter") searchRecords();
});
async function load() {
    const list = document.getElementById("list");
    try {
        loadTotal();
        const res = await fetch(buildUrl());
        if (!res.ok) {
            const text = await res.text();
            console.error("取得失敗", res.status, buildUrl(), text);
            list.textContent = "一覧の取得に失敗しました(" + res.status + ")";
            return;
        }
        const json = await res.json();
        // 配列(検索)とページ形式(通常一覧)の両方に対応
        const isPaged = !Array.isArray(json);
        const data = isPaged ? json.content : json;
        const totalPages = isPaged ? json.totalPages : 1;
        const totalElements = isPaged ? json.totalElements : json.length;
        recordsById = new Map(data.map(r => [r.id, r]));
        console.log(res.status, data);
        // 最後のページの記録を全部消した場合は、1つ前のページに戻る
        if (data.length === 0 && currentPage > 0) {
            currentPage--;
            return load();
        }
        if (data.length === 0) {
            const hasSearch = searchSubject !== "" || searchDate !== "";
            list.textContent = hasSearch
                ? "条件に一致する記録はありません。"
                : "まだ記録がありません。右のフォームから登録してみましょう。";
            updatePager(totalPages, totalElements);
            return;
        }
        list.innerHTML = data.map(r => `
                <div class="record">
                    <span>
                        <strong>${r.studyDate}</strong>
                        ${r.subject} / ${r.topic ?? ""}(${r.minutes ?? 0}分)
                        <br><small>${r.memo ?? ""}</small>
                    </span>
                    <span class="actions">
                        <button class="btn-edit" onClick="editRecord(${r.id})">編集</button>
                        <button class="btn-delete" onClick="deleteRecord(${r.id})">削除</button>
                    </span>
                </div>`).join("");
        updatePager(totalPages, totalElements);
    } catch (e) {
        list.textContent = "サーバーに接続できません。アプリが起動しているか確認してください。";
        console.error(e);
    }
}
// 編集ボタン:1件取得してフォームに反映
async function editRecord(id) {
    // 1件取得API
    const res = await fetch(`${API}/${id}`);
    if (!res.ok) {
        return;
    }
    const r = await res.json();
    document.getElementById("studyDate").value = r.studyDate ?? "";
    document.getElementById("subject").value = r.subject ?? "";
    document.getElementById("topic").value = r.topic ?? "";
    document.getElementById("minutes").value = r.minutes ?? "";
    document.getElementById("memo").value = r.memo ?? "";
    editingId = id;
    document.getElementById("formTitle").textContent = "記録を編集";
    document.getElementById("submitBtn").textContent = "更新する";
    document.getElementById("cancelBtn").style.display = "block";
    document.getElementById("subject").focus();
}
// 削除ボタン:確認モーダルを開く
async function deleteRecord(id) {
    const r = recordsById.get(id);
    if (!r) {
        alert("対象の記録が見つかりません。画面を再読み込みしてください。");
        return;
    }
    deletingId = id;
    document.getElementById("modalText").textContent =
        `${r.studyDate ?? ""} ${r.subject} / ${r.topic ?? ""}`;
    document.getElementById("modal").style.display = "flex";
}
load();
// フォームの値をまとめて取得
function getFormData() {
    return {
        studyDate: document.getElementById("studyDate").value,
        subject: document.getElementById("subject").value,
        topic: document.getElementById("topic").value,
        minutes: document.getElementById("minutes").value === "" ? null : Number(document.getElementById("minutes").value),
        memo: document.getElementById("memo").value
    };
}
// 登録・更新を切り替えて実行
async function submitForm() {
    const body = getFormData();
    const res = editingId == null
        ? await fetch(API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        })
        : await fetch(`${API}/${editingId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ...body, id: editingId })
        });
    if (!res.ok) {
        alert("保存に失敗しました(" + res.status + ")\n科目・学習内容・日付・学習時間を確認してください。");
        // 失敗時はフォームの内容を残す
        return;
    }
    resetForm();
    load();
}
// フォームを新規登録モードに戻す
function resetForm() {
    editingId = null;
    ["studyDate", "subject", "topic", "minutes", "memo"].forEach(id => {
        document.getElementById(id).value = "";
    });
    document.getElementById("formTitle").textContent = "新規登録";
    document.getElementById("submitBtn").textContent = "登録する";
    document.getElementById("cancelBtn").style.display = "none";
}
// モーダルを閉じる
function closeDeleteModal() {
    deletingId = null;
    document.getElementById("modal").style.display = "none";
}
async function confirmDelete() {
    const res = await fetch(`${API}/${deletingId}`, { method : "DELETE"});
    if (!res.ok) {
        alert("削除に失敗しました(" + res.status + ")");
        return;
    }
    // 編集中の記録を消したらフォームも戻す
    if (editingId == deletingId) resetForm();
    closeDeleteModal();
    load();
}
function updatePager(totalPages, totalElements) {
    const pages = Math.max(totalPages, 1);
    document.getElementById("pageInfo").textContent =
        `${currentPage + 1} / ${pages} ページ(全${totalElements}件)`;
    document.getElementById("prevBtn").disabled = currentPage <= 0;
    document.getElementById("nextBtn").disabled = currentPage >= totalPages - 1;
}
function changePage(delta) {
    currentPage += delta;
    load();
}
// 検索条件の有無でURLを切り替える
function buildUrl() {
    const hasSearch = searchSubject !== "" || searchDate !== "";
    if (!hasSearch) {
        return `${API}?page=${currentPage}&size=${PAGE_SIZE}`;
    }
    const params = new URLSearchParams();
    if (searchSubject !== "") params.set("subject", searchSubject);
    if (searchDate !== "") params.set("studyDate", searchDate);
    params.set("page", currentPage);
    params.set("size", PAGE_SIZE);
    return `${API}/search?${params.toString()}`;
}
// 検索実行
function searchRecords() {
    searchSubject = document.getElementById("searchSubject").value.trim();
    searchDate = document.getElementById("searchDate").value;
    currentPage = 0;
    load();
}
// 検索条件をクリア
function clearSearch() {
    document.getElementById("searchSubject").value = "";
    document.getElementById("searchDate").value = "";
    searchSubject = "";
    searchDate = "";
    currentPage = 0;
    load();
}
// 分 → 「4時間30分」形式
function formatMinutes(total) {
    const h = Math.floor(total / 60);
    const m = total % 60;
    if (h === 0) return `${m}分`;
    if (m === 0) return `${h}時間`;
    return `${h}時間${m}分`;
}
// 合計学習時間を取得して表示(検索条件を反映)
async function loadTotal() {
    const label = document.getElementById("totalLabel");
    const value = document.getElementById("totalValue");
    const params = new URLSearchParams();
    if (searchSubject !== "") params.set("subject", searchSubject);
    if (searchDate !== "") params.set("studyDate", searchDate);
    try {
        console.log("total url:", `${API}/total-minutes?${params.toString()}`);
        const res = await fetch(`${API}/total-minutes?${params.toString()}`);
        if (!res.ok) {
            value.textContent = "取得失敗(" + res.status + ")";
            return;
        }
        const json = await res.json();
        const hasSearch = searchSubject !== "" || searchDate !== "";
        label.textContent = hasSearch ? "検索結果の合計学習時間" : "合計学習時間";
        value.textContent = `${formatMinutes(json.totalMinutes)}(${json.totalMinutes}分)`;
    } catch (e) {
        value.textContent = "取得できません";
        console.error(e);
    }
}