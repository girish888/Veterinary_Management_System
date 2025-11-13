// Pet Owner Calendar JavaScript
document.addEventListener('DOMContentLoaded', function() {
    initializeOwnerCalendar();
    setupEventListeners();
});

function initializeOwnerCalendar() {
    const calendarEl = document.getElementById('ownerCalendar');
    if (!calendarEl) return;

    // Get appointment events data from the page (already formatted for calendar)
    const appointmentEventsData = window.appointmentEventsData || [];
    
    // Use the pre-formatted events data
    const events = appointmentEventsData.map(event => {
        return {
            id: event.id,
            title: event.title,
            start: event.start,
            end: event.start, // Use start time as end time for now
            className: `appointment-${event.status?.toLowerCase() || 'scheduled'}`,
            extendedProps: {
                petName: event.title.split(' with ')[0] || 'Unknown Pet',
                vetName: event.title.includes(' with Dr. ') ? event.title.split(' with Dr. ')[1] : 'Veterinarian',
                reason: event.description || 'Appointment',
                type: 'general',
                location: 'VetCare Clinic',
                notes: '',
                status: event.status || 'Scheduled'
            }
        };
    });

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek'
        },
        views: {
            dayGridMonth: {
                titleFormat: { year: 'numeric', month: 'long' }
            },
            timeGridWeek: {
                titleFormat: { year: 'numeric', month: 'long', day: 'numeric' }
            }
        },
        events: events,
        eventDisplay: 'block',
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            meridiem: false
        },
        slotMinTime: '08:00:00',
        slotMaxTime: '18:00:00',
        allDaySlot: false,
        height: 'auto',
        expandRows: true,
        dayMaxEvents: 3,
        moreLinkClick: 'popover',
        
        // Event rendering
        eventDidMount: function(info) {
            // Add tooltips
            const tooltip = new OwnerTooltip(info.el, {
                title: createOwnerEventTooltip(info.event),
                placement: 'top',
                trigger: 'hover',
                html: true
            });
        },
        
        // Event click handling
        eventClick: function(info) {
            showOwnerAppointmentDetails(info.event);
        },
        
        // Date click handling
        dateClick: function(info) {
            // Could open appointment booking modal
            console.log('Date clicked:', info.dateStr);
        },
        
        // Event drop (rescheduling)
        eventDrop: function(info) {
            handleOwnerEventReschedule(info.event, info.oldEvent);
        }
    });

    calendar.render();
    
    // Store calendar instance globally
    window.ownerCalendar = calendar;
}

function createOwnerEventTooltip(event) {
    const props = event.extendedProps;
    const time = event.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const date = event.start.toLocaleDateString([], { weekday: 'short', month: 'short', day: 'numeric' });
    
    return `
        <div class="owner-tooltip-content">
            <div class="tooltip-header">
                <span class="pet-emoji">${getPetEmoji(props.petType)}</span>
                <strong>${props.petName}</strong>
            </div>
            <div class="tooltip-details">
                <div class="tooltip-row">
                    <i class="fas fa-calendar"></i> ${date}
                </div>
                <div class="tooltip-row">
                    <i class="fas fa-clock"></i> ${time}
                </div>
                <div class="tooltip-row">
                    <i class="fas fa-user-md"></i> ${props.vetName}
                </div>
                <div class="tooltip-row">
                    <i class="fas fa-map-marker-alt"></i> ${props.location}
                </div>
            </div>
        </div>
    `;
}

function getPetEmoji(petType) {
    const emojis = {
        'Dog': '🐕',
        'Cat': '🐱',
        'Bird': '🦜',
        'Rabbit': '🐰',
        'Hamster': '🐹',
        'Fish': '🐠',
        'Horse': '🐎',
        'Guinea Pig': '🐹'
    };
    return emojis[petType] || '🐾';
}

function showOwnerAppointmentDetails(event) {
    const props = event.extendedProps;
    const modal = document.getElementById('appointmentModal');
    const modalBody = document.getElementById('appointmentModalBody');
    
    if (!modal || !modalBody) return;
    
    const time = event.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const date = event.start.toLocaleDateString([], { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
    
    modalBody.innerHTML = `
        <div class="owner-appointment-details">
            <div class="detail-header">
                <div class="appointment-type ${props.type}">
                    <span class="pet-emoji-large">${getPetEmoji(props.petType)}</span>
                    <div class="appointment-info">
                        <h5>${props.petName}</h5>
                        <p class="appointment-reason">${props.reason}</p>
                    </div>
                </div>
                <div class="appointment-meta">
                    <div class="meta-item">
                        <i class="fas fa-calendar"></i>
                        <span>${date}</span>
                    </div>
                    <div class="meta-item">
                        <i class="fas fa-clock"></i>
                        <span>${time}</span>
                    </div>
                </div>
            </div>
            
            <div class="detail-content">
                <div class="detail-section">
                    <h6><i class="fas fa-user-md"></i> Veterinarian</h6>
                    <p>${props.vetName}</p>
                </div>
                
                <div class="detail-section">
                    <h6><i class="fas fa-map-marker-alt"></i> Location</h6>
                    <p>${props.location}</p>
                </div>
                
                ${props.notes ? `
                <div class="detail-section">
                    <h6><i class="fas fa-sticky-note"></i> Notes</h6>
                    <p>${props.notes}</p>
                </div>
                ` : ''}
                
                <div class="detail-section">
                    <h6><i class="fas fa-info-circle"></i> Reminders</h6>
                    <div class="reminder-options">
                        <label class="reminder-option">
                            <input type="checkbox" checked> 
                            <span>15 minutes before</span>
                        </label>
                        <label class="reminder-option">
                            <input type="checkbox" checked> 
                            <span>1 hour before</span>
                        </label>
                        <label class="reminder-option">
                            <input type="checkbox"> 
                            <span>1 day before</span>
                        </label>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Show modal
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
}

function handleOwnerEventReschedule(newEvent, oldEvent) {
    const newDate = newEvent.start.toISOString().split('T')[0];
    const newTime = newEvent.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    
    // Show confirmation dialog
    if (confirm(`Would you like to reschedule ${newEvent.extendedProps.petName}'s appointment to ${newDate} at ${newTime}?`)) {
        // Send to backend
        console.log('Rescheduling appointment:', {
            id: newEvent.id,
            newDate: newDate,
            newTime: newTime
        });
        
        // Show success message
        showOwnerNotification('Appointment rescheduled successfully! 🎉', 'success');
    } else {
        // Revert the change
        newEvent.setStart(oldEvent.start);
        newEvent.setEnd(oldEvent.end);
    }
}

function setupEventListeners() {
    // Today button
    const todayBtn = document.getElementById('todayBtn');
    if (todayBtn) {
        todayBtn.addEventListener('click', function() {
            if (window.ownerCalendar) {
                window.ownerCalendar.today();
            }
        });
    }
    
    // View toggle buttons
    const viewButtons = document.querySelectorAll('.view-toggle .btn');
    viewButtons.forEach(button => {
        button.addEventListener('click', function() {
            const view = this.dataset.view;
            
            // Update active state
            viewButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            // Change calendar view
            if (window.ownerCalendar) {
                window.ownerCalendar.changeView(view);
            }
        });
    });
    
    // Reminder option changes
    document.addEventListener('change', function(e) {
        if (e.target.type === 'checkbox' && e.target.closest('.reminder-option')) {
            const isChecked = e.target.checked;
            const reminderText = e.target.nextElementSibling.textContent;
            
            if (isChecked) {
                showOwnerNotification(`Reminder set for ${reminderText} 🔔`, 'success');
            } else {
                showOwnerNotification(`Reminder removed for ${reminderText}`, 'info');
            }
        }
    });
}

function showOwnerNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `owner-notification owner-notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-icon">${getNotificationIcon(type)}</span>
            <span class="notification-message">${message}</span>
        </div>
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Show notification
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    // Hide and remove after 4 seconds
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 4000);
}

function getNotificationIcon(type) {
    const icons = {
        'success': '🎉',
        'info': 'ℹ️',
        'warning': '⚠️',
        'error': '❌'
    };
    return icons[type] || 'ℹ️';
}

// Owner Tooltip class for event tooltips
class OwnerTooltip {
    constructor(element, options) {
        this.element = element;
        this.options = options;
        this.tooltip = null;
        this.init();
    }
    
    init() {
        this.element.addEventListener('mouseenter', this.show.bind(this));
        this.element.addEventListener('mouseleave', this.hide.bind(this));
    }
    
    show() {
        this.createTooltip();
        this.positionTooltip();
        this.tooltip.classList.add('show');
    }
    
    hide() {
        if (this.tooltip) {
            this.tooltip.classList.remove('show');
            setTimeout(() => {
                if (this.tooltip && this.tooltip.parentNode) {
                    this.tooltip.parentNode.removeChild(this.tooltip);
                }
                this.tooltip = null;
            }, 200);
        }
    }
    
    createTooltip() {
        this.tooltip = document.createElement('div');
        this.tooltip.className = 'owner-event-tooltip';
        this.tooltip.innerHTML = this.options.title;
        document.body.appendChild(this.tooltip);
    }
    
    positionTooltip() {
        const rect = this.element.getBoundingClientRect();
        const tooltipRect = this.tooltip.getBoundingClientRect();
        
        let top = rect.top - tooltipRect.height - 10;
        let left = rect.left + (rect.width / 2) - (tooltipRect.width / 2);
        
        // Adjust if tooltip goes off screen
        if (top < 10) top = rect.bottom + 10;
        if (left < 10) left = 10;
        if (left + tooltipRect.width > window.innerWidth - 10) {
            left = window.innerWidth - tooltipRect.width - 10;
        }
        
        this.tooltip.style.top = top + 'px';
        this.tooltip.style.left = left + 'px';
    }
}

// Add CSS for owner tooltips and notifications
const ownerStyle = document.createElement('style');
ownerStyle.textContent = `
    .owner-event-tooltip {
        position: fixed;
        z-index: 10000;
        background: rgba(16, 185, 129, 0.95);
        color: white;
        padding: 12px 16px;
        border-radius: 12px;
        font-size: 13px;
        max-width: 280px;
        pointer-events: none;
        opacity: 0;
        transition: opacity 0.3s ease;
        box-shadow: 0 8px 25px rgba(16, 185, 129, 0.3);
        backdrop-filter: blur(10px);
    }
    
    .owner-event-tooltip.show {
        opacity: 1;
    }
    
    .owner-event-tooltip::after {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 6px solid transparent;
        border-top-color: rgba(16, 185, 129, 0.95);
    }
    
    .owner-tooltip-content {
        text-align: center;
    }
    
    .tooltip-header {
        margin-bottom: 8px;
        font-size: 14px;
        font-weight: 600;
    }
    
    .pet-emoji {
        font-size: 16px;
        margin-right: 6px;
    }
    
    .tooltip-row {
        margin: 4px 0;
        font-size: 12px;
        opacity: 0.9;
    }
    
    .owner-notification {
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        border-radius: 16px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
        padding: 1rem 1.5rem;
        z-index: 10000;
        transform: translateX(100%);
        transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        border: 1px solid #e5e7eb;
    }
    
    .owner-notification.show {
        transform: translateX(0);
    }
    
    .owner-notification-success {
        border-left: 4px solid #10b981;
    }
    
    .owner-notification-info {
        border-left: 4px solid #3b82f6;
    }
    
    .owner-notification-warning {
        border-left: 4px solid #f59e0b;
    }
    
    .owner-notification-error {
        border-left: 4px solid #ef4444;
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }
    
    .notification-icon {
        font-size: 1.2rem;
    }
    
    .notification-message {
        font-weight: 500;
        color: #1f2937;
    }
    
    .owner-appointment-details {
        padding: 1rem 0;
    }
    
    .detail-header {
        margin-bottom: 2rem;
        padding-bottom: 1.5rem;
        border-bottom: 1px solid #e5e7eb;
    }
    
    .appointment-type {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 1rem;
    }
    
    .pet-emoji-large {
        font-size: 3rem;
    }
    
    .appointment-info h5 {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
    }
    
    .appointment-reason {
        margin: 0.25rem 0 0 0;
        color: #6b7280;
        font-size: 1rem;
    }
    
    .appointment-meta {
        display: flex;
        gap: 2rem;
    }
    
    .meta-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: #6b7280;
    }
    
    .meta-item i {
        color: #10b981;
    }
    
    .detail-section {
        margin-bottom: 1.5rem;
    }
    
    .detail-section h6 {
        color: #374151;
        margin-bottom: 0.75rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .detail-section h6 i {
        color: #10b981;
    }
    
    .detail-section p {
        margin: 0.25rem 0;
        color: #6b7280;
    }
    
    .reminder-options {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }
    
    .reminder-option {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        cursor: pointer;
        padding: 0.5rem;
        border-radius: 8px;
        transition: background-color 0.2s ease;
    }
    
    .reminder-option:hover {
        background-color: #f9fafb;
    }
    
    .reminder-option input[type="checkbox"] {
        width: 18px;
        height: 18px;
        accent-color: #10b981;
    }
    
    .reminder-option span {
        color: #374151;
        font-weight: 500;
    }
`;
document.head.appendChild(ownerStyle);
