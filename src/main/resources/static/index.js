"use strict";
const API = "/api/study-records";
// nullなら新規登録、値があれば編集中
let editingId = null;
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
                        <button class="btn-edit" onClick="editRecord(${r.id}">編集</button>
                        <button class="btn-delete" onClick="deleteRecord(${r.id})">削除</button>
                    </span>
                </div>`).join("");
}
async function createRecord() {
    const body = {
        studyDate: document.getElementById("studyDate").value,
        subject: document.getElementById("subject").value,
        topic: document.getElementById("topic").value,
        minutes: Number(document.getElementById("minutes").value),
        memo: document.getElementById("memo").value
    };
    await fetch(API, {
        method:"POST",
        headers:{"Content-Type": "application/json"},
        body: JSON.stringify(body)
    });
    load();
}
async function editRecord(id) {
    // 1件取得
    const cur = await (await fetch(`${API}/${id}`)).json();
    const minutes = prompt("新しい学習時間(分)", cur.minutes);
    if (minutes == null) return;
    // 更新
    await fetch(`${API}/${id}`, {
        method: "PUT",
        headers:{ "Content-Type":"application/json" },
        body: JSON.stringify({...cur, minutes: Number(minutes) })
    });
    load();
}
async function deleteRecord(id) {
    if (!confirm("この記録を削除しますか?")) return;
    await fetch(`${API}/${id}`, { method:"DELETE" });
    load();
}
load();
// フォームの値をまとめて取得
function getFormData() {
    return {
        studyDate: document.getElementById("studyDate").value,
        subject: document.getElementById("subject").value,
        topic: document.getElementById("topic").value,
        minutes: Number(document.getElementById("minutes").value),
        memo: document.getElementById("memo").value
    };
}
// 登録・更新を切り替えて実行
async function submitForm() {
    const body = getFormData();
    if (editingId == null) {
        // 新規登録
        await fetch(API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });
    } else {
        // 更新
        await fetch(`${API}/${editingId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ...body, })
        })
    }
}