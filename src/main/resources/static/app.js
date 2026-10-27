const API = '/api/tasks';
const form = document.getElementById('task-form');
const tbody = document.querySelector('#tasks tbody');

async function loadTasks() {
    const res = await fetch(API);
    const tasks = await res.json();
    tbody.innerHTML = '';
    for (const t of tasks) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${t.id}</td>
            <td>${t.title}</td>
            <td>${t.description ?? ''}</td>
            <td>${t.status}</td>
            <td>
                <button onclick="editTask(${t.id})">✏️</button>
                <button onclick="deleteTask(${t.id})">🗑️</button>
            </td>`;
        tbody.appendChild(tr);
    }
}

form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('task-id').value;
    const body = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        status: document.getElementById('status').value
    };
    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API}/${id}` : API;
    await fetch(url, {
        method,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body)
    });
    form.reset();
    document.getElementById('task-id').value = '';
    loadTasks();
});

document.getElementById('cancel').onclick = () => {
    form.reset();
    document.getElementById('task-id').value = '';
};

async function editTask(id) {
    const res = await fetch(`${API}/${id}`);
    const t = await res.json();
    document.getElementById('task-id').value = t.id;
    document.getElementById('title').value = t.title;
    document.getElementById('description').value = t.description ?? '';
    document.getElementById('status').value = t.status;
}

async function deleteTask(id) {
    if (!confirm('Удалить?')) return;
    await fetch(`${API}/${id}`, {method: 'DELETE'});
    loadTasks();
}

loadTasks();