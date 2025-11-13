function initializeCalendar(calendarEl, events) {
    return new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        events: events,
        eventClick: function(info) {
            showAppointmentDetails(info.event);
        },
        eventTimeFormat: {
            hour: '2-digit',
            minute: '2-digit',
            meridiem: false
        },
        slotMinTime: '08:00:00',
        slotMaxTime: '20:00:00',
        allDaySlot: false,
        height: 'auto'
    });
}

function showAppointmentDetails(event) {
    // Implement appointment details modal
    const modal = document.getElementById('appointmentModal');
    const modalContent = document.getElementById('appointmentModalContent');
    
    modalContent.innerHTML = `
        <h3>${event.title}</h3>
        <p>Date: ${event.start.toLocaleDateString()}</p>
        <p>Time: ${event.start.toLocaleTimeString()}</p>
        <p>Pet: ${event.extendedProps.petName}</p>
        <p>Owner: ${event.extendedProps.ownerName}</p>
        <p>Reason: ${event.extendedProps.reason}</p>
    `;
    
    modal.style.display = 'block';
} 