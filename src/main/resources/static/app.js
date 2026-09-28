/**
 * FilmJury — Frontend Application Logic
 * Connects to the Spring Boot REST API at /api/*
 */

// ============================================
// API Configuration
// ============================================
const API_BASE = '/api';

const API = {
    entries: `${API_BASE}/entries`,
    judges: `${API_BASE}/judges`,
    scorecards: `${API_BASE}/scorecards`,
    criteria: `${API_BASE}/criteria`,
    leaderboard: `${API_BASE}/entries/leaderboard`,
};

// ============================================
// State
// ============================================
let currentPage = 'dashboard';
let entriesData = [];
let judgesData = [];
let criteriaData = [];
let scorecardsData = [];

// ============================================
// Initialization
// ============================================
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initMenuToggle();
    loadDashboard();
});

// ============================================
// Navigation
// ============================================
function initNavigation() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const page = item.dataset.page;
            navigateTo(page);
        });
    });
}

function navigateTo(page) {
    currentPage = page;

    // Update nav active state
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.toggle('active', item.dataset.page === page);
    });

    // Update page visibility
    document.querySelectorAll('.page').forEach(p => {
        p.classList.remove('active');
    });
    const pageEl = document.getElementById(`page-${page}`);
    if (pageEl) {
        pageEl.classList.add('active');
    }

    // Update title
    const titles = {
        dashboard: 'Dashboard',
        entries: 'Film Entries',
        judges: 'Judges Panel',
        scorecards: 'Score Cards',
        leaderboard: 'Leaderboard'
    };
    document.getElementById('page-title').textContent = titles[page] || page;

    // Load page data
    switch (page) {
        case 'dashboard': loadDashboard(); break;
        case 'entries': loadEntries(); break;
        case 'judges': loadJudges(); break;
        case 'scorecards': loadScoreCards(); break;
        case 'leaderboard': loadLeaderboard(); break;
    }

    // Close mobile sidebar
    document.getElementById('sidebar').classList.remove('open');
}

// ============================================
// Mobile Menu Toggle
// ============================================
function initMenuToggle() {
    document.getElementById('menu-toggle').addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('open');
    });

    // Close sidebar when clicking outside on mobile
    document.getElementById('main-content').addEventListener('click', () => {
        document.getElementById('sidebar').classList.remove('open');
    });
}

// ============================================
// API Helper
// ============================================
async function apiFetch(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: { 'Content-Type': 'application/json' },
            ...options,
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
        }

        // Handle 204 No Content
        if (response.status === 204) return null;

        return await response.json();
    } catch (error) {
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            showToast('Cannot connect to API. Is the server running?', 'error');
            updateConnectionStatus(false);
        }
        throw error;
    }
}

function updateConnectionStatus(connected) {
    const indicator = document.querySelector('.status-indicator');
    const dot = indicator.querySelector('.status-dot');
    const text = indicator.querySelector('span:last-child');
    
    if (connected) {
        indicator.style.borderColor = 'rgba(52, 211, 153, 0.2)';
        indicator.style.background = 'rgba(52, 211, 153, 0.1)';
        dot.style.background = 'var(--success)';
        text.textContent = 'API Connected';
    } else {
        indicator.style.borderColor = 'rgba(248, 113, 113, 0.2)';
        indicator.style.background = 'rgba(248, 113, 113, 0.1)';
        dot.style.background = 'var(--error)';
        text.textContent = 'API Disconnected';
    }
}

// ============================================
// DASHBOARD
// ============================================
async function loadDashboard() {
    try {
        const [entries, judges, scorecards, criteria, leaderboard] = await Promise.all([
            apiFetch(API.entries),
            apiFetch(API.judges),
            apiFetch(API.scorecards),
            apiFetch(API.criteria),
            apiFetch(API.leaderboard),
        ]);

        entriesData = entries;
        judgesData = judges;
        scorecardsData = scorecards;
        criteriaData = criteria;

        updateConnectionStatus(true);

        // Animate stat counters
        animateCounter('stat-entries', entries.length);
        animateCounter('stat-judges', judges.length);
        animateCounter('stat-scorecards', scorecards.length);
        animateCounter('stat-criteria', criteria.length);

        // Render top 5 leaderboard preview
        renderLeaderboardPreview(leaderboard.slice(0, 5));
    } catch (error) {
        console.error('Dashboard load error:', error);
    }
}

function animateCounter(elementId, target) {
    const el = document.getElementById(elementId);
    const duration = 600;
    const start = parseInt(el.textContent) || 0;
    const startTime = performance.now();

    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        // Ease-out cubic
        const eased = 1 - Math.pow(1 - progress, 3);
        el.textContent = Math.round(start + (target - start) * eased);

        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }

    requestAnimationFrame(update);
}

function renderLeaderboardPreview(items) {
    const container = document.getElementById('dashboard-leaderboard');
    
    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No scores submitted yet. Start judging!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = items.map(item => createLeaderboardItemHTML(item)).join('');
}

// ============================================
// ENTRIES
// ============================================
async function loadEntries() {
    try {
        entriesData = await apiFetch(API.entries);
        renderEntries(entriesData);
    } catch (error) {
        console.error('Load entries error:', error);
    }
}

function renderEntries(entries) {
    const grid = document.getElementById('entries-grid');

    if (!entries || entries.length === 0) {
        grid.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="48" height="48">
                    <polygon points="10,4 16,8 10,12"/>
                    <rect x="2" y="2" width="20" height="20" rx="3"/>
                </svg>
                <p>No film entries yet. Add your first entry!</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = entries.map(entry => `
        <div class="entry-card" id="entry-card-${entry.id}">
            <div class="entry-card-header">
                <h3 class="entry-card-title">${escapeHtml(entry.title)}</h3>
                <span class="entry-card-id">#${entry.id}</span>
            </div>
            <span class="genre-tag">${escapeHtml(entry.genre)}</span>
            <div class="entry-card-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
                    <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
                </svg>
                <a href="${escapeHtml(entry.videoLink)}" target="_blank" style="color: inherit; text-decoration: none;">${escapeHtml(entry.videoLink)}</a>
            </div>
            <div class="entry-card-actions">
                <button class="btn btn-ghost btn-sm" onclick="openEditEntry(${entry.id})">
                    ✏️ Edit
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteEntry(${entry.id})">
                    🗑️ Delete
                </button>
                <button class="btn btn-ghost btn-sm" onclick="viewEntryScore(${entry.id})" style="margin-left: auto;">
                    📊 Score
                </button>
            </div>
        </div>
    `).join('');
}

async function handleEntrySubmit(event) {
    event.preventDefault();

    const data = {
        title: document.getElementById('entry-title').value,
        genre: document.getElementById('entry-genre').value,
        videoLink: document.getElementById('entry-video').value,
    };

    try {
        await apiFetch(API.entries, {
            method: 'POST',
            body: JSON.stringify(data),
        });

        showToast('Film entry added successfully!', 'success');
        closeModal('entry-modal');
        document.getElementById('entry-form').reset();
        loadEntries();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function openEditEntry(id) {
    const entry = entriesData.find(e => e.id === id);
    if (!entry) return;

    document.getElementById('edit-entry-id').value = entry.id;
    document.getElementById('edit-entry-title').value = entry.title;
    document.getElementById('edit-entry-genre').value = entry.genre;
    document.getElementById('edit-entry-video').value = entry.videoLink;

    openModal('edit-entry-modal');
}

async function handleEditEntrySubmit(event) {
    event.preventDefault();

    const id = document.getElementById('edit-entry-id').value;
    const data = {
        title: document.getElementById('edit-entry-title').value,
        genre: document.getElementById('edit-entry-genre').value,
        videoLink: document.getElementById('edit-entry-video').value,
    };

    try {
        await apiFetch(`${API.entries}/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data),
        });

        showToast('Entry updated successfully!', 'success');
        closeModal('edit-entry-modal');
        loadEntries();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function deleteEntry(id) {
    if (!confirm('Are you sure you want to delete this entry?')) return;

    try {
        await apiFetch(`${API.entries}/${id}`, { method: 'DELETE' });
        showToast('Entry deleted successfully!', 'success');
        loadEntries();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function viewEntryScore(id) {
    try {
        const score = await apiFetch(`${API.entries}/${id}/score`);
        const msg = score.message
            ? score.message
            : `Final Score: ${score.finalScore} (${score.numberOfJudges} judge${score.numberOfJudges !== 1 ? 's' : ''})`;
        showToast(`${score.title}: ${msg}`, 'info');
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// ============================================
// JUDGES
// ============================================
async function loadJudges() {
    try {
        judgesData = await apiFetch(API.judges);
        renderJudges(judgesData);
    } catch (error) {
        console.error('Load judges error:', error);
    }
}

function renderJudges(judges) {
    const grid = document.getElementById('judges-grid');

    if (!judges || judges.length === 0) {
        grid.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="48" height="48">
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                </svg>
                <p>No judges registered yet. Add your first judge!</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = judges.map(judge => {
        const initials = judge.name.split(' ').map(n => n[0]).join('').slice(0, 2);
        return `
            <div class="judge-card" id="judge-card-${judge.id}">
                <div class="judge-avatar">${escapeHtml(initials)}</div>
                <div class="judge-info">
                    <div class="judge-name">${escapeHtml(judge.name)}</div>
                    <div class="judge-email">${escapeHtml(judge.email)}</div>
                </div>
                <span class="judge-id">#${judge.id}</span>
            </div>
        `;
    }).join('');
}

async function handleJudgeSubmit(event) {
    event.preventDefault();

    const data = {
        name: document.getElementById('judge-name').value,
        email: document.getElementById('judge-email').value,
    };

    try {
        await apiFetch(API.judges, {
            method: 'POST',
            body: JSON.stringify(data),
        });

        showToast('Judge added successfully!', 'success');
        closeModal('judge-modal');
        document.getElementById('judge-form').reset();
        loadJudges();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// ============================================
// SCORECARDS
// ============================================
async function loadScoreCards() {
    try {
        const [scorecards, entries, judges, criteria] = await Promise.all([
            apiFetch(API.scorecards),
            apiFetch(API.entries),
            apiFetch(API.judges),
            apiFetch(API.criteria),
        ]);

        scorecardsData = scorecards;
        entriesData = entries;
        judgesData = judges;
        criteriaData = criteria;

        renderScoreCards(scorecards);
    } catch (error) {
        console.error('Load scorecards error:', error);
    }
}

function renderScoreCards(scorecards) {
    const tbody = document.getElementById('scorecards-tbody');
    const emptyState = document.getElementById('scorecards-empty');
    const table = document.getElementById('scorecards-table');

    if (!scorecards || scorecards.length === 0) {
        table.style.display = 'none';
        emptyState.style.display = 'flex';
        return;
    }

    table.style.display = 'table';
    emptyState.style.display = 'none';

    tbody.innerHTML = scorecards.map(sc => `
        <tr>
            <td>${sc.id}</td>
            <td>${escapeHtml(sc.entry?.title || 'N/A')}</td>
            <td>${escapeHtml(sc.judge?.name || 'N/A')}</td>
            <td>${escapeHtml(sc.criterion?.name || 'N/A')}</td>
            <td><span class="score-badge">${sc.score}</span></td>
        </tr>
    `).join('');
}

async function handleScorecardSubmit(event) {
    event.preventDefault();

    const data = {
        entryId: parseInt(document.getElementById('sc-entry').value),
        judgeId: parseInt(document.getElementById('sc-judge').value),
        criterionId: parseInt(document.getElementById('sc-criterion').value),
        score: parseInt(document.getElementById('sc-score').value),
    };

    try {
        await apiFetch(API.scorecards, {
            method: 'POST',
            body: JSON.stringify(data),
        });

        showToast('Score submitted successfully!', 'success');
        closeModal('scorecard-modal');
        document.getElementById('scorecard-form').reset();
        document.getElementById('score-display').textContent = '0';
        loadScoreCards();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function updateScoreDisplay(value) {
    document.getElementById('score-display').textContent = value;
}

// Update max score when criterion changes
document.addEventListener('DOMContentLoaded', () => {
    const criterionSelect = document.getElementById('sc-criterion');
    if (criterionSelect) {
        criterionSelect.addEventListener('change', function () {
            const criterion = criteriaData.find(c => c.id === parseInt(this.value));
            if (criterion) {
                const slider = document.getElementById('sc-score');
                slider.max = criterion.maxScore;
                document.getElementById('sc-max-label').textContent = criterion.maxScore;
            }
        });
    }
});

// ============================================
// LEADERBOARD
// ============================================
async function loadLeaderboard() {
    try {
        const leaderboard = await apiFetch(API.leaderboard);
        renderLeaderboard(leaderboard);
    } catch (error) {
        console.error('Load leaderboard error:', error);
    }
}

function renderLeaderboard(items) {
    const container = document.getElementById('leaderboard-container');

    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="48" height="48">
                    <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5C7 4 7 7 7 7"/>
                    <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5C17 4 17 7 17 7"/>
                </svg>
                <p>No leaderboard data yet. Submit some scores first!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = items.map(item => createLeaderboardItemHTML(item)).join('');
}

function createLeaderboardItemHTML(item) {
    const rankClass = item.rank <= 3 ? `rank-${item.rank}` : '';
    const maxPossible = 100; // max possible score (matches criterion maxScore)
    const barWidth = Math.min((item.finalScore / maxPossible) * 100, 100);

    return `
        <div class="leaderboard-item ${rankClass}">
            <div class="rank-badge">${item.rank}</div>
            <div class="leaderboard-info">
                <div class="leaderboard-title">${escapeHtml(item.title)}</div>
                <div class="leaderboard-meta">
                    <span class="genre-tag" style="margin: 0; padding: 2px 8px; font-size: 0.65rem;">${escapeHtml(item.genre)}</span>
                    <span>${item.numberOfJudges} judge${item.numberOfJudges !== 1 ? 's' : ''}</span>
                </div>
            </div>
            <div class="leaderboard-score-section">
                <div class="leaderboard-score">${item.finalScore}</div>
                <div class="score-bar-bg">
                    <div class="score-bar-fill" style="width: ${barWidth}%;"></div>
                </div>
            </div>
        </div>
    `;
}

// ============================================
// MODALS
// ============================================
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.classList.add('active');

    // Populate dropdowns for scorecard modal
    if (modalId === 'scorecard-modal') {
        populateScorecardDropdowns();
    }

    // Close on overlay click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) closeModal(modalId);
    });

    // Close on Escape key
    const handler = (e) => {
        if (e.key === 'Escape') {
            closeModal(modalId);
            document.removeEventListener('keydown', handler);
        }
    };
    document.addEventListener('keydown', handler);
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

async function populateScorecardDropdowns() {
    try {
        const [entries, judges, criteria] = await Promise.all([
            apiFetch(API.entries),
            apiFetch(API.judges),
            apiFetch(API.criteria),
        ]);

        entriesData = entries;
        judgesData = judges;
        criteriaData = criteria;

        const entrySelect = document.getElementById('sc-entry');
        entrySelect.innerHTML = '<option value="">Select Entry</option>' +
            entries.map(e => `<option value="${e.id}">${escapeHtml(e.title)}</option>`).join('');

        const judgeSelect = document.getElementById('sc-judge');
        judgeSelect.innerHTML = '<option value="">Select Judge</option>' +
            judges.map(j => `<option value="${j.id}">${escapeHtml(j.name)}</option>`).join('');

        const criterionSelect = document.getElementById('sc-criterion');
        criterionSelect.innerHTML = '<option value="">Select Criterion</option>' +
            criteria.map(c => `<option value="${c.id}">${escapeHtml(c.name)} (max: ${c.maxScore})</option>`).join('');
    } catch (error) {
        console.error('Error populating dropdowns:', error);
    }
}

// ============================================
// TOAST NOTIFICATIONS
// ============================================
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');

    const icons = {
        success: '✅',
        error: '❌',
        info: 'ℹ️',
    };

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${icons[type] || ''}</span><span>${escapeHtml(message)}</span>`;

    container.appendChild(toast);

    // Auto-remove after 4 seconds
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// ============================================
// UTILITIES
// ============================================
function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
