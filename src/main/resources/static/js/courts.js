/**
 * courts.js — Court listing and booking functionality
 */

// ───────────────────────────────────────────────
// DOM references
// ───────────────────────────────────────────────
const courtsGrid      = document.getElementById('courts-grid');
const filterStatus    = document.getElementById('filter-status');
const searchInput     = document.getElementById('search-input');
const bookingModal    = document.getElementById('booking-modal');
const bookingForm     = document.getElementById('booking-form');
const bookingCourtInfo = document.getElementById('booking-court-info');
const bookingCourtId  = document.getElementById('booking-court-id');
const closeModalBtn   = document.getElementById('close-modal');

let allCourts = [];

// ───────────────────────────────────────────────
// Load & render courts
// ───────────────────────────────────────────────
async function loadCourts() {
  renderSkeletons(courtsGrid, 6, '320px');
  try {
    allCourts = await fetchAPI('/courts');
    renderCourts(allCourts);
  } catch (err) {
    courtsGrid.innerHTML = `
      <div class="empty-state" style="grid-column:1/-1">
        <div class="empty-icon">⚠️</div>
        <h3>Không thể tải danh sách sân</h3>
        <p>${err.message}</p>
      </div>`;
    showToast('Lỗi tải danh sách sân. Kiểm tra kết nối API.', 'error');
  }
}

function renderCourts(courts) {
  if (!courts.length) {
    courtsGrid.innerHTML = `
      <div class="empty-state" style="grid-column:1/-1">
        <div class="empty-icon">🏸</div>
        <h3>Không có sân nào</h3>
        <p>Chưa có sân nào được thêm vào hệ thống.</p>
      </div>`;
    return;
  }
  courtsGrid.innerHTML = courts.map(court => `
    <div class="card court-card" id="court-${court.id}">
      <div class="court-img">🏸</div>
      <div class="court-info">
        <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:8px;">
          <h3>${escapeHtml(court.name)}</h3>
          ${courtStatusBadge(court.status)}
        </div>
        <p class="desc">${escapeHtml(court.description || 'Sân cầu lông tiêu chuẩn')}</p>
        <div class="price">${formatCurrency(court.pricePerHour)} <span>/ giờ</span></div>
        <button class="btn btn-primary btn-block"
          onclick="openBooking(${court.id},'${escapeHtml(court.name)}',${court.pricePerHour},'${court.status}')"
          ${court.status !== 'AVAILABLE' ? 'disabled' : ''}>
          ${court.status === 'AVAILABLE' ? '📅 Đặt sân ngay' : '🚫 Không khả dụng'}
        </button>
      </div>
    </div>`).join('');
}

// ───────────────────────────────────────────────
// Filtering
// ───────────────────────────────────────────────
function applyFilters() {
  const status = filterStatus?.value || '';
  const search = searchInput?.value.trim().toLowerCase() || '';
  let filtered = allCourts;
  if (status) filtered = filtered.filter(c => c.status === status);
  if (search) filtered = filtered.filter(c =>
    c.name.toLowerCase().includes(search) ||
    (c.description || '').toLowerCase().includes(search)
  );
  renderCourts(filtered);
}

filterStatus?.addEventListener('change', applyFilters);
searchInput?.addEventListener('input', applyFilters);

// ───────────────────────────────────────────────
// Booking Modal
// ───────────────────────────────────────────────
function openBooking(courtId, courtName, pricePerHour, status) {
  if (status !== 'AVAILABLE') { showToast('Sân này hiện không khả dụng', 'warning'); return; }
  bookingCourtId.value = courtId;
  bookingCourtInfo.innerHTML = `
    <div style="background:rgba(0,212,170,0.06);border:1px solid rgba(0,212,170,0.2);border-radius:10px;padding:12px 16px;margin-bottom:1rem;">
      <strong>🏸 ${escapeHtml(courtName)}</strong>
      <span style="color:var(--accent);font-weight:700;float:right">${formatCurrency(pricePerHour)}/giờ</span>
    </div>`;
  // Set min date = today
  const today = new Date().toISOString().split('T')[0];
  document.getElementById('booking-date').min = today;
  document.getElementById('booking-date').value = today;
  bookingModal.classList.add('open');
}

closeModalBtn?.addEventListener('click', () => bookingModal.classList.remove('open'));
bookingModal?.addEventListener('click', e => {
  if (e.target === bookingModal) bookingModal.classList.remove('open');
});

// ───────────────────────────────────────────────
// Submit booking
// ───────────────────────────────────────────────
bookingForm?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const submitBtn = bookingForm.querySelector('[type="submit"]');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Đang xử lý...';

  const payload = {
    courtId:      parseInt(bookingCourtId.value),
    customerName: document.getElementById('customer-name').value.trim(),
    customerPhone: document.getElementById('customer-phone').value.trim(),
    bookingDate:  document.getElementById('booking-date').value,
    startTime:    document.getElementById('start-time').value,
    endTime:      document.getElementById('end-time').value,
  };

  try {
    const result = await fetchAPI('/bookings', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    bookingModal.classList.remove('open');
    bookingForm.reset();
    showToast(`✅ Đặt sân thành công! Tổng tiền: ${formatCurrency(result.totalPrice)}`, 'success', 6000);
    await loadCourts();
  } catch (err) {
    showToast(err.message || 'Đặt sân thất bại. Vui lòng thử lại.', 'error');
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = '📅 Xác nhận đặt sân';
  }
});

// ───────────────────────────────────────────────
// Escape HTML helper
// ───────────────────────────────────────────────
function escapeHtml(str) {
  const div = document.createElement('div');
  div.appendChild(document.createTextNode(str || ''));
  return div.innerHTML;
}

// ───────────────────────────────────────────────
// Init
// ───────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', loadCourts);
