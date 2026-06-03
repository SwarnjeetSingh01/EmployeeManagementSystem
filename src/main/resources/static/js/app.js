/* ═══════════════════════════════════════════════════════════════════════════
   EMS — app.js
   Vanilla JS: Toast, Modal, Real-time Search, Charts, Counter Animation
   ═══════════════════════════════════════════════════════════════════════════ */

(function () {
  'use strict';

  /* ── Toast Notification System ──────────────────────────────────────────── */
  const ToastManager = {
    container: null,

    init() {
      this.container = document.getElementById('toast-container');
      if (!this.container) return;

      // Read flash messages rendered by Thymeleaf into hidden data element
      const dataEl = document.getElementById('toast-data');
      if (dataEl) {
        const success = dataEl.dataset.success;
        const error   = dataEl.dataset.error;
        if (success && success !== '') this.show(success, 'success');
        if (error   && error   !== '') this.show(error,   'error');
      }
    },

    show(message, type = 'success', duration = 4000) {
      if (!this.container) return;

      const icons = {
        success: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20,6 9,17 4,12"/></svg>`,
        error:   `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>`,
        warning: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>`,
      };

      const titles = { success: 'Success', error: 'Error', warning: 'Warning' };

      const toast = document.createElement('div');
      toast.className = `toast toast-${type}`;
      toast.innerHTML = `
        <div class="toast-icon">${icons[type] || icons.success}</div>
        <div class="toast-content">
          <div class="toast-title">${titles[type] || 'Notification'}</div>
          <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close" onclick="EMS.ToastManager.dismiss(this.parentElement)" aria-label="Close">✕</button>
        <div class="toast-progress"></div>
      `;

      this.container.appendChild(toast);

      const timer = setTimeout(() => this.dismiss(toast), duration);
      toast._timer = timer;
    },

    dismiss(toast) {
      if (!toast || toast.classList.contains('toast-exit')) return;
      clearTimeout(toast._timer);
      toast.classList.add('toast-exit');
      toast.addEventListener('animationend', () => toast.remove(), { once: true });
    }
  };

  /* ── Delete Confirmation Modal ───────────────────────────────────────────── */
  const DeleteModal = {
    overlay: null,
    form: null,
    nameEl: null,

    init() {
      this.overlay = document.getElementById('deleteModal');
      this.form    = document.getElementById('deleteForm');
      this.nameEl  = document.getElementById('deleteEmployeeName');

      // Close on backdrop click
      if (this.overlay) {
        this.overlay.addEventListener('click', (e) => {
          if (e.target === this.overlay) this.close();
        });
      }

      // Close on Escape
      document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') this.close();
      });
    },

    open(entityId, entityName, actionUrl) {
      if (!this.overlay) return;
      if (this.nameEl) this.nameEl.textContent = entityName;
      if (this.form)   this.form.action = actionUrl;

      // Inject CSRF token
      const csrfMeta = document.querySelector('meta[name="_csrf"]');
      const csrfParam = document.querySelector('meta[name="_csrf_parameter"]');
      if (csrfMeta && csrfParam) {
        let csrfInput = this.form.querySelector('input[name="' + csrfParam.content + '"]');
        if (!csrfInput) {
          csrfInput = document.createElement('input');
          csrfInput.type = 'hidden';
          csrfInput.name = csrfParam.content;
          this.form.appendChild(csrfInput);
        }
        csrfInput.value = csrfMeta.content;
      }

      this.overlay.classList.remove('hidden');
      document.body.style.overflow = 'hidden';
    },

    close() {
      if (!this.overlay) return;
      this.overlay.classList.add('hidden');
      document.body.style.overflow = '';
    }
  };

  /* ── Real-time Search with Debounce ─────────────────────────────────────── */
  const SearchManager = {
    timerId: null,

    init() {
      const searchInput = document.getElementById('searchInput');
      const searchForm  = document.getElementById('searchForm');
      if (!searchInput || !searchForm) return;

      searchInput.addEventListener('input', () => {
        clearTimeout(this.timerId);
        this.timerId = setTimeout(() => {
          // Reset to page 0 on new search
          const pageInput = searchForm.querySelector('input[name="page"]');
          if (pageInput) pageInput.value = '0';
          searchForm.requestSubmit();
        }, 350);
      });

      // Trigger on select changes immediately
      const selects = searchForm.querySelectorAll('select');
      selects.forEach(sel => {
        sel.addEventListener('change', () => {
          const pageInput = searchForm.querySelector('input[name="page"]');
          if (pageInput) pageInput.value = '0';
          searchForm.requestSubmit();
        });
      });
    }
  };

  /* ── Stat Counter Animation ──────────────────────────────────────────────── */
  const CounterAnimation = {
    init() {
      const stats = document.querySelectorAll('[data-counter]');
      if (!stats.length) return;

      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            this.animate(entry.target);
            observer.unobserve(entry.target);
          }
        });
      }, { threshold: 0.3 });

      stats.forEach(el => observer.observe(el));
    },

    animate(el) {
      const target   = parseInt(el.dataset.counter, 10) || 0;
      const duration = 1200;
      const start    = performance.now();

      const step = (now) => {
        const elapsed  = now - start;
        const progress = Math.min(elapsed / duration, 1);
        const ease     = 1 - Math.pow(1 - progress, 3); // ease-out cubic
        el.textContent = Math.round(ease * target).toLocaleString();
        if (progress < 1) requestAnimationFrame(step);
      };

      requestAnimationFrame(step);
    }
  };

  /* ── Dashboard Charts ────────────────────────────────────────────────────── */
  const ChartManager = {
    COLORS: [
      '#3B82F6', '#8B5CF6', '#EC4899', '#F59E0B',
      '#10B981', '#06B6D4', '#6366F1', '#EF4444'
    ],

    init() {
      const deptCanvas   = document.getElementById('deptChart');
      const salaryCanvas = document.getElementById('salaryChart');
      if (!deptCanvas && !salaryCanvas) return;

      fetch('/api/dashboard/stats')
        .then(r => r.json())
        .then(data => {
          if (deptCanvas)   this.renderDeptChart(deptCanvas, data);
          if (salaryCanvas) this.renderSalaryChart(salaryCanvas, data);
        })
        .catch(() => {
          /* Charts fail silently if fetch fails */
        });
    },

    renderDeptChart(canvas, data) {
      const breakdown = data.departmentBreakdown || [];
      if (!breakdown.length) {
        canvas.closest('.chart-container').innerHTML =
          '<p style="color:var(--text-tertiary);font-size:13px;">No department data yet.</p>';
        return;
      }

      new Chart(canvas, {
        type: 'doughnut',
        data: {
          labels: breakdown.map(d => d.name),
          datasets: [{
            data: breakdown.map(d => d.count),
            backgroundColor: this.COLORS.slice(0, breakdown.length),
            borderWidth: 0,
            hoverOffset: 6,
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '68%',
          plugins: {
            legend: {
              position: 'right',
              labels: {
                color: '#8B949E',
                font: { family: 'Inter', size: 12 },
                boxWidth: 10,
                padding: 16,
              }
            },
            tooltip: {
              backgroundColor: '#161B22',
              borderColor: '#21262D',
              borderWidth: 1,
              titleColor: '#E6EDF3',
              bodyColor: '#8B949E',
              padding: 10,
              callbacks: {
                label: ctx => ` ${ctx.label}: ${ctx.parsed} employees`
              }
            }
          }
        }
      });
    },

    renderSalaryChart(canvas, data) {
      const dist = data.salaryDistribution || {};
      const labels = Object.keys(dist);
      const values = Object.values(dist);

      new Chart(canvas, {
        type: 'bar',
        data: {
          labels,
          datasets: [{
            label: 'Employees',
            data: values,
            backgroundColor: [
              'rgba(59,130,246,0.7)',
              'rgba(99,102,241,0.7)',
              'rgba(139,92,246,0.7)',
            ],
            borderColor: [
              '#3B82F6', '#6366F1', '#8B5CF6'
            ],
            borderWidth: 1,
            borderRadius: 6,
            borderSkipped: false,
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false },
            tooltip: {
              backgroundColor: '#161B22',
              borderColor: '#21262D',
              borderWidth: 1,
              titleColor: '#E6EDF3',
              bodyColor: '#8B949E',
              padding: 10,
            }
          },
          scales: {
            x: {
              grid: { color: 'rgba(255,255,255,0.04)' },
              ticks: { color: '#8B949E', font: { family: 'Inter', size: 12 } },
              border: { color: '#21262D' }
            },
            y: {
              grid: { color: 'rgba(255,255,255,0.04)' },
              ticks: { color: '#8B949E', font: { family: 'Inter', size: 12 }, stepSize: 1 },
              border: { color: '#21262D' },
              beginAtZero: true,
            }
          }
        }
      });
    }
  };

  /* ── Department Bar Widths ───────────────────────────────────────────────── */
  const DeptBars = {
    init() {
      document.querySelectorAll('[data-dept-bar]').forEach(el => {
        const pct = parseFloat(el.dataset.deptBar) || 0;
        // Animate width after paint
        requestAnimationFrame(() => {
          setTimeout(() => { el.style.width = Math.min(pct, 100) + '%'; }, 100);
        });
      });
    }
  };

  /* ── Stagger animations for table rows ──────────────────────────────────── */
  const StaggerRows = {
    init() {
      const rows = document.querySelectorAll('.data-table tbody tr');
      rows.forEach((row, i) => {
        row.style.opacity = '0';
        row.style.transform = 'translateY(8px)';
        row.style.transition = 'opacity 250ms ease, transform 250ms ease';
        setTimeout(() => {
          row.style.opacity = '1';
          row.style.transform = 'translateY(0)';
        }, i * 40);
      });
    }
  };

  /* ── Public API ──────────────────────────────────────────────────────────── */
  window.EMS = {
    ToastManager,
    DeleteModal,

    openDeleteModal(id, name, url) {
      DeleteModal.open(id, name, url);
    },
    closeDeleteModal() {
      DeleteModal.close();
    }
  };

  /* ── Bootstrap ───────────────────────────────────────────────────────────── */
  document.addEventListener('DOMContentLoaded', () => {
    ToastManager.init();
    DeleteModal.init();
    SearchManager.init();
    CounterAnimation.init();
    ChartManager.init();
    DeptBars.init();
    StaggerRows.init();
  });

})();
