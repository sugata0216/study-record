"use strict";
const API = "/api/study-records";
// nullなら新規登録、値があれば編集中
let editingId = null;
// 削除対象のID
let deletingId = null;
async function load() {
    const res = await fetch(API);
    const data = await res.json();
    document.getElementById("list").innerHTML = data.map(r => `
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
}
// 編集ボタン:1件取得してフォームに反映
async function editRecord(id) {
    // 1件取得
    const res = await fetch(`${API}/${id}`);
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
    // 1件取得して内容を表示
    const res = await fetch(`${API}/${id}`);
    if (res.ok) {
        alert("記録の取得に失敗しました(" + res.status + ")");
        return;
    }
    const r = await res.json();
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
// 削除ボタン:確認モーダルを開く
async function deleteRecord(id) {
    // 1件取得して内容を表示
    const res = await fetch(`${API}/${id}`);
    if (!res.ok) {
        alert("記録の取得に失敗しました(" + res.status + ")");
        return;
    }
    const r = await res.json();
    deletingId = id;
    
}