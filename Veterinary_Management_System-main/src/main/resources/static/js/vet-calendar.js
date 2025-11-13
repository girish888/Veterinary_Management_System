// Veterinarian Calendar JavaScript
document.addEventListener('DOMContentLoaded', function() {
    initializeVetCalendar();
    setupEventListeners();
});

function initializeVetCalendar() {
    const calendarEl = document.getElementById('vetCalendar');
    if (!calendarEl) return;

    // Get appointment events data from backend
    let events = [];
    
    // Check if appointment events data is available from Thymeleaf
    if (typeof window.appointmentEventsData !== 'undefined' && window.appointmentEventsData) {
        events = window.appointmentEventsData;
    }
    
    // If no data from backend, use sample data for demonstration
    if (events.length === 0) {
        events = [
            {
                id: 1,
                title: 'Buddy - Annual Checkup',
                start: '2024-01-15T09:00:00',
                end: '2024-01-15T09:30:00',
                className: 'checkup',
                extendedProps: {
                    petName: 'Buddy',
                    ownerName: 'John Smith',
                    reason: 'Annual Checkup',
                    type: 'checkup',
                    location: 'Exam Room 1',
                    notes: 'Routine wellness exam, vaccinations due'
                }
            }
        ];
    }

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        views: {
            dayGridMonth: {
                titleFormat: { year: 'numeric', month: 'long' }
            },
            timeGridWeek: {
                titleFormat: { year: 'numeric', month: 'long', day: 'numeric' }
            },
            timeGridDay: {
                titleFormat: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
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
        dayMaxEvents: 4,
        moreLinkClick: 'popover',
        
        // Event rendering
        eventDidMount: function(info) {
            // Add tooltips
            const tooltip = new Tooltip(info.el, {
                title: createEventTooltip(info.event),
                placement: 'top',
                trigger: 'hover',
                html: true,
                template: '<div class="tooltip vet-tooltip" role="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>'
            });
        },
        
        // Event click handling
        eventClick: function(info) {
            showAppointmentDetails(info.event);
        },
        
        // Date click handling
        dateClick: function(info) {
            // Could open appointment creation modal
            console.log('Date clicked:', info.dateStr);
        },
        
        // Event drop (rescheduling)
        eventDrop: function(info) {
            handleEventReschedule(info.event, info.oldEvent);
        },
        
        // Event resize (duration change)
        eventResize: function(info) {
            handleEventDurationChange(info.event, info.oldEvent);
        },
        
        // Loading states
        loading: function(isLoading) {
            if (isLoading) {
                calendarEl.classList.add('fc-loading');
            } else {
                calendarEl.classList.remove('fc-loading');
            }
        }
    });

    calendar.render();
    
    // Store calendar instance globally
    window.vetCalendar = calendar;
}

function createEventTooltip(event) {
    const props = event.extendedProps;
    const time = event.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    
    return `
        <div class="vet-tooltip-content">
            <div class="tooltip-header">
                <strong>${props.petName}</strong> - ${props.reason}
            </div>
            <div class="tooltip-details">
                <div><i class="fas fa-clock"></i> ${time}</div>
                <div><i class="fas fa-user"></i> ${props.ownerName}</div>
                <div><i class="fas fa-map-marker-alt"></i> ${props.location}</div>
                ${props.notes ? `<div><i class="fas fa-sticky-note"></i> ${props.notes}</div>` : ''}
            </div>
        </div>
    `;
}

function showAppointmentDetails(event) {
    const props = event.extendedProps;
    const modal = document.getElementById('appointmentModal');
    const modalBody = document.getElementById('appointmentModalBody');
    
    if (!modal || !modalBody) return;
    
    const time = event.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const date = event.start.toLocaleDateString([], { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
    
    modalBody.innerHTML = `
        <div class="appointment-details">
            <div class="detail-header">
                <div class="appointment-type ${props.type}">
                    <i class="fas ${getAppointmentIcon(props.type)}"></i>
                    ${props.reason}
                </div>
                <div class="appointment-time">
                    <i class="fas fa-calendar"></i> ${date}
                </div>
                <div class="appointment-time">
                    <i class="fas fa-clock"></i> ${time}
                </div>
            </div>
            
            <div class="detail-content">
                <div class="detail-section">
                    <h6><i class="fas fa-paw"></i> Pet Information</h6>
                    <p><strong>Name:</strong> ${props.petName}</p>
                    <p><strong>Owner:</strong> ${props.ownerName}</p>
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
            </div>
        </div>
    `;
    
    // Show modal
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
}

function getAppointmentIcon(type) {
    const icons = {
        'checkup': 'fa-stethoscope',
        'vaccination': 'fa-syringe',
        'surgery': 'fa-procedures',
        'followup': 'fa-calendar-check',
        'emergency': 'fa-ambulance',
        'consultation': 'fa-user-md'
    };
    return icons[type] || 'fa-calendar';
}

function handleEventReschedule(newEvent, oldEvent) {
    const newDate = newEvent.start.toISOString().split('T')[0];
    const newTime = newEvent.start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    
    // Show confirmation dialog
    if (confirm(`Reschedule appointment to ${newDate} at ${newTime}?`)) {
        // Send to backend
        console.log('Rescheduling appointment:', {
            id: newEvent.id,
            newDate: newDate,
            newTime: newTime
        });
        
        // Could show success message
        showNotification('Appointment rescheduled successfully!', 'success');
    } else {
        // Revert the change
        newEvent.setStart(oldEvent.start);
        newEvent.setEnd(oldEvent.end);
    }
}

function handleEventDurationChange(newEvent, oldEvent) {
    const newDuration = Math.round((newEvent.end - newEvent.start) / (1000 * 60));
    
    if (confirm(`Change appointment duration to ${newDuration} minutes?`)) {
        // Send to backend
        console.log('Duration changed:', {
            id: newEvent.id,
            newDuration: newDuration
        });
        
        showNotification('Appointment duration updated!', 'success');
    } else {
        // Revert the change
        newEvent.setEnd(oldEvent.end);
    }
}

function setupEventListeners() {
    // Today button
    const todayBtn = document.getElementById('todayBtn');
    if (todayBtn) {
        todayBtn.addEventListener('click', function() {
            if (window.vetCalendar) {
                window.vetCalendar.today();
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
            if (window.vetCalendar) {
                window.vetCalendar.changeView(view);
            }
        });
    });
}

function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'info-circle'}"></i>
            <span>${message}</span>
        </div>
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Show notification
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    // Hide and remove after 3 seconds
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Tooltip class for event tooltips
class Tooltip {
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
        this.tooltip.className = 'vet-event-tooltip';
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

// Add CSS for tooltips and notifications
const style = document.createElement('style');
style.textContent = `
    .vet-event-tooltip {
        position: fixed;
        z-index: 10000;
        background: rgba(0, 0, 0, 0.9);
        color: white;
        padding: 8px 12px;
        border-radius: 6px;
        font-size: 12px;
        max-width: 250px;
        pointer-events: none;
        opacity: 0;
        transition: opacity 0.2s ease;
    }
    
    .vet-event-tooltip.show {
        opacity: 1;
    }
    
    .vet-event-tooltip::after {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 5px solid transparent;
        border-top-color: rgba(0, 0, 0, 0.9);
    }
    
    .notification {
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        padding: 1rem;
        z-index: 10000;
        transform: translateX(100%);
        transition: transform 0.3s ease;
    }
    
    .notification.show {
        transform: translateX(0);
    }
    
    .notification-success {
        border-left: 4px solid #10b981;
    }
    
    .notification-info {
        border-left: 4px solid #3b82f6;
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .notification-content i {
        color: #10b981;
    }
    
    .appointment-details {
        padding: 1rem 0;
    }
    
    .detail-header {
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #e5e7eb;
    }
    
    .appointment-type {
        font-size: 1.25rem;
        font-weight: 700;
        margin-bottom: 0.5rem;
        padding: 0.5rem 1rem;
        border-radius: 8px;
        display: inline-block;
        color: white;
    }
    
    .appointment-type.checkup { background: #10b981; }
    .appointment-type.vaccination { background: #3b82f6; }
    .appointment-type.surgery { background: #ef4444; }
    .appointment-type.followup { background: #8b5cf6; }
    .appointment-type.emergency { background: #f59e0b; }
    .appointment-type.consultation { background: #06b6d4; }
    
    .appointment-time {
        margin: 0.25rem 0;
        color: #6b7280;
    }
    
    .detail-section {
        margin-bottom: 1.5rem;
    }
    
    .detail-section h6 {
        color: #374151;
        margin-bottom: 0.5rem;
        font-weight: 600;
    }
    
    .detail-section p {
        margin: 0.25rem 0;
        color: #6b7280;
    }
`;
document.head.appendChild(style);
