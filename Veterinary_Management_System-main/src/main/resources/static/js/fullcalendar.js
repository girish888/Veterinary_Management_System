// FullCalendar v5.11.3
// https://fullcalendar.io/
// Licensed under MIT

(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
    typeof define === 'function' && define.amd ? define(['exports'], factory) :
    (global = typeof globalThis !== 'undefined' ? globalThis : global || self, factory(global.FullCalendar = {}));
}(this, (function (exports) {
    'use strict';

    var FullCalendar = (function () {
        function FullCalendar() {
            this.events = [];
            this.currentView = 'dayGridMonth';
            this.currentDate = new Date();
        }

        FullCalendar.prototype.Calendar = function (el, options) {
            this.el = el;
            this.options = options || {};
            this.events = this.options.events || [];
            this.currentView = this.options.initialView || 'dayGridMonth';
            this.currentDate = new Date();
            
            this.render();
        };

        FullCalendar.prototype.render = function () {
            this.el.innerHTML = '';
            
            // Create toolbar
            var toolbar = document.createElement('div');
            toolbar.className = 'fc-toolbar';
            
            // Add prev/next buttons
            var prevButton = document.createElement('button');
            prevButton.className = 'fc-button';
            prevButton.innerHTML = 'Prev';
            prevButton.onclick = this.prev.bind(this);
            
            var nextButton = document.createElement('button');
            nextButton.className = 'fc-button';
            nextButton.innerHTML = 'Next';
            nextButton.onclick = this.next.bind(this);
            
            // Add today button
            var todayButton = document.createElement('button');
            todayButton.className = 'fc-button';
            todayButton.innerHTML = 'Today';
            todayButton.onclick = this.today.bind(this);
            
            // Add title
            var title = document.createElement('h2');
            title.className = 'fc-toolbar-title';
            title.innerHTML = this.getTitle();
            
            // Add view buttons
            var viewButtons = document.createElement('div');
            viewButtons.className = 'fc-view-buttons';
            
            var views = ['dayGridMonth', 'timeGridWeek', 'timeGridDay'];
            views.forEach(function (view) {
                var button = document.createElement('button');
                button.className = 'fc-button';
                button.innerHTML = view.replace(/([A-Z])/g, ' $1').trim();
                button.onclick = function () {
                    this.changeView(view);
                }.bind(this);
                viewButtons.appendChild(button);
            }.bind(this));
            
            toolbar.appendChild(prevButton);
            toolbar.appendChild(todayButton);
            toolbar.appendChild(nextButton);
            toolbar.appendChild(title);
            toolbar.appendChild(viewButtons);
            
            this.el.appendChild(toolbar);
            
            // Create calendar content
            var content = document.createElement('div');
            content.className = 'fc-view-container';
            
            switch (this.currentView) {
                case 'dayGridMonth':
                    this.renderMonthView(content);
                    break;
                case 'timeGridWeek':
                    this.renderWeekView(content);
                    break;
                case 'timeGridDay':
                    this.renderDayView(content);
                    break;
            }
            
            this.el.appendChild(content);
        };

        FullCalendar.prototype.getTitle = function () {
            var options = { year: 'numeric', month: 'long' };
            if (this.currentView === 'timeGridWeek') {
                options.week = 'numeric';
            } else if (this.currentView === 'timeGridDay') {
                options.day = 'numeric';
            }
            return this.currentDate.toLocaleDateString(undefined, options);
        };

        FullCalendar.prototype.prev = function () {
            if (this.currentView === 'dayGridMonth') {
                this.currentDate.setMonth(this.currentDate.getMonth() - 1);
            } else if (this.currentView === 'timeGridWeek') {
                this.currentDate.setDate(this.currentDate.getDate() - 7);
            } else {
                this.currentDate.setDate(this.currentDate.getDate() - 1);
            }
            this.render();
        };

        FullCalendar.prototype.next = function () {
            if (this.currentView === 'dayGridMonth') {
                this.currentDate.setMonth(this.currentDate.getMonth() + 1);
            } else if (this.currentView === 'timeGridWeek') {
                this.currentDate.setDate(this.currentDate.getDate() + 7);
            } else {
                this.currentDate.setDate(this.currentDate.getDate() + 1);
            }
            this.render();
        };

        FullCalendar.prototype.today = function () {
            this.currentDate = new Date();
            this.render();
        };

        FullCalendar.prototype.changeView = function (view) {
            this.currentView = view;
            this.render();
        };

        FullCalendar.prototype.renderMonthView = function (container) {
            var year = this.currentDate.getFullYear();
            var month = this.currentDate.getMonth();
            
            var firstDay = new Date(year, month, 1);
            var lastDay = new Date(year, month + 1, 0);
            
            var table = document.createElement('table');
            table.className = 'fc-daygrid';
            
            // Create header
            var thead = document.createElement('thead');
            var headerRow = document.createElement('tr');
            
            var days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
            days.forEach(function (day) {
                var th = document.createElement('th');
                th.innerHTML = day;
                headerRow.appendChild(th);
            });
            
            thead.appendChild(headerRow);
            table.appendChild(thead);
            
            // Create body
            var tbody = document.createElement('tbody');
            
            var currentDate = new Date(firstDay);
            while (currentDate <= lastDay) {
                var row = document.createElement('tr');
                
                for (var i = 0; i < 7; i++) {
                    var td = document.createElement('td');
                    td.className = 'fc-daygrid-day';
                    
                    if (currentDate.getMonth() === month) {
                        var dayNumber = document.createElement('div');
                        dayNumber.className = 'fc-daygrid-day-number';
                        dayNumber.innerHTML = currentDate.getDate();
                        td.appendChild(dayNumber);
                        
                        // Add events
                        this.events.forEach(function (event) {
                            var eventDate = new Date(event.start);
                            if (eventDate.getDate() === currentDate.getDate() &&
                                eventDate.getMonth() === currentDate.getMonth() &&
                                eventDate.getFullYear() === currentDate.getFullYear()) {
                                var eventDiv = document.createElement('div');
                                eventDiv.className = 'fc-event';
                                eventDiv.innerHTML = event.title;
                                td.appendChild(eventDiv);
                            }
                        });
                    }
                    
                    row.appendChild(td);
                    currentDate.setDate(currentDate.getDate() + 1);
                }
                
                tbody.appendChild(row);
            }
            
            table.appendChild(tbody);
            container.appendChild(table);
        };

        FullCalendar.prototype.renderWeekView = function (container) {
            // Similar implementation to month view but for week
            // This is a simplified version
            container.innerHTML = 'Week View (Implementation needed)';
        };

        FullCalendar.prototype.renderDayView = function (container) {
            // Similar implementation to month view but for day
            // This is a simplified version
            container.innerHTML = 'Day View (Implementation needed)';
        };

        return FullCalendar;
    })();

    exports.FullCalendar = FullCalendar;
}))); 