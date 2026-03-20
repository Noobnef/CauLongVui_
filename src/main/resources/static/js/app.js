/**
 * app.js — Shared utilities for CauLongVui front-end
 */

const API_BASE = '/api';

// ───────────────────────────────────────────────
// Generic fetch wrapper
// ───────────────────────────────────────────────
async function fetchAPI(endpoint, options = {}) {
  const defaultOptions = {
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    ...options,
  };
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, defaultOptions);
    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || `HTTP ${res.status}`);
    }
    return json.data;
  } catch (err) {
    console.error(`[API Error] ${endpoint}:`, err.message);
    throw err;
  }
}

// ───────────────────────────────────────────────
// Formatting helpers
// ───────────────────────────────────────────────
function formatCurrency(amount) {
  if (amount == null) return '—';
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency', currency: 'VND', maximumFractionDigits: 0
  }).format(amount);
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const [y, m, d] = dateStr.split('-');
  return `${d}/${m}/${y}`;
}

function formatTime(timeStr) {
  if (!timeStr) return '—';
  const [h, m] = timeStr.split(':');
  return `${h}:${m}`;
}

// ───────────────────────────────────────────────
// Toast notifications
// ───────────────────────────────────────────────
const toastContainer = (() => {
  let el = document.getElementById('toast-container');
  if (!el) {
    el = document.createElement('div');
    el.id = 'toast-container';
    document.body.appendChild(el);
  }
  return el;
})();

function showToast(message, type = 'success', duration = 4000) {
  const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span class="toast-icon">${icons[type] || '💬'}</span>
    <span class="toast-msg">${message}</span>`;
  toastContainer.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

// ───────────────────────────────────────────────
// Status / badge helpers
// ───────────────────────────────────────────────
function courtStatusBadge(status) {
  const map = {
    AVAILABLE:   { cls: 'badge-success', label: '🟢 Còn trống' },
    BOOKED:      { cls: 'badge-warning', label: '🟡 Đã đặt' },
    MAINTENANCE: { cls: 'badge-danger',  label: '🔴 Bảo trì' },
  };
  const s = map[status] || { cls: 'badge-muted', label: status };
  return `<span class="badge ${s.cls}">${s.label}</span>`;
}

function bookingStatusBadge(status) {
  const map = {
    PENDING:   { cls: 'badge-warning', label: '⏳ Chờ xác nhận' },
    CONFIRMED: { cls: 'badge-success', label: '✅ Đã xác nhận' },
    CANCELLED: { cls: 'badge-danger',  label: '❌ Đã hủy' },
    COMPLETED: { cls: 'badge-muted',   label: '🏆 Hoàn thành' },
  };
  const s = map[status] || { cls: 'badge-muted', label: status };
  return `<span class="badge ${s.cls}">${s.label}</span>`;
}

// ───────────────────────────────────────────────
// Skeleton loader helper
// ───────────────────────────────────────────────
function renderSkeletons(container, count = 6, height = '280px') {
  container.innerHTML = Array.from({ length: count }, () =>
    `<div class="skeleton" style="height:${height};border-radius:14px;"></div>`
  ).join('');
}

// ───────────────────────────────────────────────
// Active nav link
// ───────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const path = location.pathname.replace(/\/$/, '') || '/index.html';
  document.querySelectorAll('.nav-links a').forEach(a => {
    const href = a.getAttribute('href').replace(/\/$/, '');
    if (path.endsWith(href) || (path === '/' && href === '/') || (path.endsWith('index.html') && (href === '/' || href === '/index.html'))) {
      a.classList.add('active');
    }
  });
});
