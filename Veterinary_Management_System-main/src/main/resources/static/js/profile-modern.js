/**
 * Profile Modern JavaScript - Veterinary Management System
 * Enhanced functionality for profile pages
 */

class ProfileManager {
    constructor() {
        this.initializeElements();
        this.bindEvents();
        this.setupPhotoUpload();
        this.setupFormValidation();
        this.setupAccessibility();

        // Test photo upload endpoint accessibility
        setTimeout(() => {
            this.testPhotoEndpoint();
        }, 1000);
    }

    initializeElements() {
        // Photo upload elements
        this.photoInput = document.getElementById('profilePhotoInput');
        this.photoPreview = document.getElementById('photoPreview');
        this.currentPhoto = document.getElementById('currentPhoto');
        this.currentInitial = document.getElementById('currentInitial');
        this.photoActions = document.getElementById('photoActions');
        this.savePhotoBtn = document.getElementById('savePhotoBtn');
        this.cancelPhotoBtn = document.getElementById('cancelPhotoBtn');

        // Form elements
        this.editProfileForm = document.getElementById('editProfileForm');
        this.saveProfileBtn = document.getElementById('saveProfileBtn');

        // Overlay and toast elements
        this.loadingOverlay = document.getElementById('loadingOverlay');
        this.successToast = document.getElementById('successToast');
        this.errorToast = document.getElementById('errorToast');
        this.errorMessage = document.getElementById('errorMessage');

        // Validation elements
        this.formInputs = document.querySelectorAll('.form-input, .form-textarea');

        // Initialize existing profile photos with error handling
        this.initializeProfilePhotos();
    }

    initializeProfilePhotos() {
        // Add error handling to existing profile photos
        if (this.currentPhoto) {
            this.currentPhoto.onerror = () => {
                console.warn('Profile photo failed to load, falling back to initial');
                this.fallbackToInitial();
            };
        }

        // Ensure proper display state
        this.updatePhotoDisplayState();
    }

    updatePhotoDisplayState() {
        if (this.currentPhoto && this.currentPhoto.src && !this.currentPhoto.src.includes('data:')) {
            // Has a real photo URL, show it
            this.currentPhoto.style.display = 'block';
            if (this.currentInitial) this.currentInitial.style.display = 'none';
        } else if (this.currentInitial) {
            // No photo, show initial
            this.currentInitial.style.display = 'block';
            if (this.currentPhoto) this.currentPhoto.style.display = 'none';
        }
    }

    bindEvents() {
        // Photo upload events
        if (this.photoInput) {
            this.photoInput.addEventListener('change', (e) => this.handlePhotoUpload(e));
        }

        if (this.savePhotoBtn) {
            this.savePhotoBtn.addEventListener('click', () => this.savePhoto());
        }

        if (this.cancelPhotoBtn) {
            this.cancelPhotoBtn.addEventListener('click', () => this.cancelPhotoUpload());
        }

        // Form submission events
        if (this.editProfileForm) {
            this.editProfileForm.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }

        // Form validation events
        this.formInputs.forEach(input => {
            input.addEventListener('blur', () => this.validateField(input));
            input.addEventListener('input', () => this.clearFieldError(input));
        });

        // Keyboard navigation
        document.addEventListener('keydown', (e) => this.handleKeyboardNavigation(e));
    }

    setupPhotoUpload() {
        if (!this.photoInput) return;

        // Drag and drop functionality
        const photoWrapper = document.querySelector('.photo-wrapper');
        if (photoWrapper) {
            photoWrapper.addEventListener('dragover', (e) => {
                e.preventDefault();
                photoWrapper.classList.add('drag-over');
            });

            photoWrapper.addEventListener('dragleave', () => {
                photoWrapper.classList.remove('drag-over');
            });

            photoWrapper.addEventListener('drop', (e) => {
                e.preventDefault();
                photoWrapper.classList.remove('drag-over');
                const files = e.dataTransfer.files;
                if (files.length > 0) {
                    this.photoInput.files = files;
                    this.handlePhotoUpload({ target: this.photoInput });
                }
            });
        }
    }

    handlePhotoUpload(event) {
        const file = event.target.files[0];
        if (!file) return;

        console.log('Photo upload event triggered:', {
            fileName: file.name,
            fileSize: file.size,
            fileType: file.type
        });

        // Validate file
        if (!this.validatePhotoFile(file)) return;

        // Show preview
        this.showPhotoPreview(file);

        // Show action buttons
        if (this.photoActions) {
            this.photoActions.style.display = 'flex';
        }
    }

    validatePhotoFile(file) {
        const maxSize = 5 * 1920 * 1080; // 5MB
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];

        console.log('Validating photo file:', {
            fileName: file.name,
            fileSize: file.size,
            fileType: file.type,
            maxSize: maxSize,
            allowedTypes: allowedTypes
        });

        if (!allowedTypes.includes(file.type)) {
            this.showError('Please select a valid image file (JPG, PNG, or GIF)');
            return false;
        }

        if (file.size > maxSize) {
            this.showError('Image size must be less than 5MB');
            return false;
        }

        console.log('Photo file validation passed');
        return true;
    }

    showPhotoPreview(file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            if (this.photoPreview) {
                this.photoPreview.src = e.target.result;
                this.photoPreview.style.display = 'block';
            }

            // Hide current photo/initial
            if (this.currentPhoto) this.currentPhoto.style.display = 'none';
            if (this.currentInitial) this.currentInitial.style.display = 'none';
        };
        reader.readAsDataURL(file);
    }

    savePhoto() {
        if (!this.photoInput || !this.photoInput.files[0]) {
            this.showError('No photo selected');
            return;
        }

        const file = this.photoInput.files[0];
        console.log('Attempting to save photo:', {
            fileName: file.name,
            fileSize: file.size,
            fileType: file.type
        });

        this.showLoading('Saving photo...');

        // Create FormData for photo upload
        const formData = new FormData();
        formData.append('profilePhotoFile', file);

        // Get CSRF token if available
        const csrfToken = this.getCsrfToken();
        if (csrfToken) {
            formData.append('_csrf', csrfToken);
            console.log('CSRF token added to request');
        } else {
            console.warn('No CSRF token found');
        }

        // Log FormData contents for debugging
        for (let [key, value] of formData.entries()) {
            console.log('FormData entry:', key, value);
        }

        // Send photo to server
        fetch('/profile/photo', {
            method: 'POST',
            body: formData,
            headers: {
                // Don't set Content-Type for FormData, let browser set it with boundary
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            console.log('Photo upload response status:', response.status);
            console.log('Photo upload response headers:', response.headers);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            return response.json();
        })
        .then(data => {
            console.log('Photo upload response data:', data);
            this.hideLoading();
            if (data.success) {
                this.showSuccess('Photo updated successfully!');

                // Update the photo display with the new image
                if (data.photoUrl) {
                    this.updatePhotoDisplay(data.photoUrl);
                }

                this.hidePhotoActions();

                // Force refresh the page after a short delay to ensure all elements are updated
                setTimeout(() => {
                    console.log('Refreshing page to show updated photo');
                    window.location.reload();
                }, 2000);
            } else {
                this.showError(data.message || 'Failed to update photo');
                this.revertToOriginalPhoto();
            }
        })
        .catch(error => {
            console.error('Photo upload error:', error);
            this.hideLoading();
            this.showError('Failed to update photo. Please try again. Error: ' + error.message);
        });
    }

    getCsrfToken() {
        // Try to get CSRF token from meta tag
        const metaTag = document.querySelector('meta[name="_csrf"]');
        if (metaTag) {
            return metaTag.getAttribute('content');
        }

        // Try to get CSRF token from hidden input
        const csrfInput = document.querySelector('input[name="_csrf"]');
        if (csrfInput) {
            return csrfInput.value;
        }

        // Try to get CSRF token from form
        const form = document.querySelector('form');
        if (form) {
            const formCsrfInput = form.querySelector('input[name="_csrf"]');
            if (formCsrfInput) {
                return formCsrfInput.value;
            }
        }

        console.warn('CSRF token not found');
        return null;
    }

    updatePhotoDisplay(photoUrl) {
        console.log('Updating photo display with URL:', photoUrl);

        // Create a new image element to test loading
        const testImage = new Image();
        testImage.onload = () => {
            console.log('Image loaded successfully, updating display');
            this.showCurrentPhoto(photoUrl);
        };
        testImage.onerror = () => {
            console.error('Failed to load image:', photoUrl);
            this.showError('Failed to load uploaded image. Please try again.');
            this.revertToOriginalPhoto();
        };

        // Add cache busting to ensure fresh image
        const cacheBustedUrl = photoUrl + '?t=' + Date.now();
        testImage.src = cacheBustedUrl;
    }

    showCurrentPhoto(photoUrl) {
        if (this.currentPhoto) {
            // Add cache busting to ensure fresh image
            const cacheBustedUrl = photoUrl + '?t=' + Date.now();

            // Set a timeout for image loading
            const imageLoadTimeout = setTimeout(() => {
                console.warn('Image loading timeout, falling back to initial');
                this.fallbackToInitial();
            }, 10000); // 10 second timeout

            this.currentPhoto.onload = () => {
                clearTimeout(imageLoadTimeout);
                console.log('Current photo loaded successfully');
            };

            this.currentPhoto.onerror = () => {
                clearTimeout(imageLoadTimeout);
                console.error('Failed to display current photo, falling back to initial');
                this.fallbackToInitial();
            };

            this.currentPhoto.src = cacheBustedUrl;
            this.currentPhoto.style.display = 'block';
        }

        if (this.photoPreview) {
            this.photoPreview.style.display = 'none';
        }

        if (this.currentInitial) {
            this.currentInitial.style.display = 'none';
        }
    }

    fallbackToInitial() {
        console.log('Falling back to initial avatar');
        if (this.currentPhoto) {
            this.currentPhoto.style.display = 'none';
        }
        if (this.photoPreview) {
            this.photoPreview.style.display = 'none';
        }
        if (this.currentInitial) {
            this.currentInitial.style.display = 'block';
        }
    }

    revertToOriginalPhoto() {
        console.log('Reverting to original photo');
        if (this.currentPhoto) {
            this.currentPhoto.style.display = 'block';
        }
        if (this.photoPreview) {
            this.photoPreview.style.display = 'none';
        }
        if (this.currentInitial) {
            this.currentInitial.style.display = 'none';
        }
    }

    cancelPhotoUpload() {
        // Reset file input
        if (this.photoInput) {
            this.photoInput.value = '';
        }

        // Hide preview and show original
        if (this.photoPreview) {
            this.photoPreview.style.display = 'none';
        }

        if (this.currentPhoto) {
            this.currentPhoto.style.display = 'block';
        } else if (this.currentInitial) {
            this.currentInitial.style.display = 'block';
        }

        this.hidePhotoActions();
    }

    hidePhotoActions() {
        if (this.photoActions) {
            this.photoActions.style.display = 'none';
        }
    }

    setupFormValidation() {
        // Real-time validation
        this.formInputs.forEach(input => {
            input.addEventListener('input', () => {
                this.validateField(input);
            });
        });
    }

    validateField(input) {
        const value = input.value.trim();
        const fieldName = input.name || input.id;
        let isValid = true;
        let errorMessage = '';

        // Remove existing error
        this.clearFieldError(input);

        // Required field validation
        if (input.hasAttribute('required') && !value) {
            isValid = false;
            errorMessage = `${this.getFieldLabel(input)} is required`;
        }

        // Email validation
        if (fieldName === 'email' && value && !this.isValidEmail(value)) {
            isValid = false;
            errorMessage = 'Please enter a valid email address';
        }

        // Mobile validation
        if (fieldName === 'mobile' && value && !this.isValidMobile(value)) {
            isValid = false;
            errorMessage = 'Please enter a valid mobile number';
        }

        // Show error if invalid
        if (!isValid) {
            this.showFieldError(input, errorMessage);
        }

        return isValid;
    }

    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    isValidMobile(mobile) {
        const mobileRegex = /^[\+]?[1-9][\d]{0,15}$/;
        return mobileRegex.test(mobile.replace(/[\s\-\(\)]/g, ''));
    }

    getFieldLabel(input) {
        const label = input.previousElementSibling;
        if (label && label.tagName === 'LABEL') {
            return label.textContent.replace('*', '').trim();
        }
        return input.placeholder || input.name || 'This field';
    }

    showFieldError(input, message) {
        // Remove existing error
        this.clearFieldError(input);

        // Add error class
        input.classList.add('error');

        // Create and show error message
        const errorSpan = document.createElement('span');
        errorSpan.className = 'error-message';
        errorSpan.textContent = message;
        errorSpan.style.color = 'var(--danger-color)';
        errorSpan.style.fontSize = '0.875rem';
        errorSpan.style.marginTop = '0.25rem';
        errorSpan.style.display = 'block';

        input.parentNode.appendChild(errorSpan);
    }

    clearFieldError(input) {
        input.classList.remove('error');
        const errorSpan = input.parentNode.querySelector('.error-message');
        if (errorSpan) {
            errorSpan.remove();
        }
    }

    handleFormSubmit(event) {
        event.preventDefault();

        // Validate all fields
        let isValid = true;
        this.formInputs.forEach(input => {
            if (!this.validateField(input)) {
                isValid = false;
            }
        });

        if (!isValid) {
            this.showError('Please fix the errors before submitting');
            return;
        }

        // Show loading
        this.showLoading('Saving changes...');

        // Submit form
        const formData = new FormData(event.target);

        fetch(event.target.action, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                return response.json();
            }
        })
        .then(data => {
            this.hideLoading();
            if (data && data.success) {
                this.showSuccess('Profile updated successfully!');
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
            } else if (data) {
                this.showError(data.message || 'Failed to update profile');
            }
        })
        .catch(error => {
            this.hideLoading();
            this.showError('Failed to update profile. Please try again.');
            console.error('Form submission error:', error);
        });
    }

    setupAccessibility() {
        // Add ARIA labels
        this.formInputs.forEach(input => {
            const label = input.previousElementSibling;
            if (label && label.tagName === 'LABEL') {
                input.setAttribute('aria-labelledby', label.id || label.textContent);
            }
        });

        // Add skip links for keyboard navigation
        this.addSkipLinks();
    }

    addSkipLinks() {
        const skipLink = document.createElement('a');
        skipLink.href = '#main-content';
        skipLink.textContent = 'Skip to main content';
        skipLink.className = 'skip-link';
        skipLink.style.cssText = `
            position: absolute;
            top: -40px;
            left: 6px;
            background: var(--primary-color);
            color: white;
            padding: 8px;
            text-decoration: none;
            border-radius: 4px;
            z-index: 10001;
        `;
        skipLink.addEventListener('focus', () => {
            skipLink.style.top = '6px';
        });
        skipLink.addEventListener('blur', () => {
            skipLink.style.top = '-40px';
        });

        document.body.insertBefore(skipLink, document.body.firstChild);
    }

    handleKeyboardNavigation(event) {
        // Escape key to close modals/overlays
        if (event.key === 'Escape') {
            if (this.loadingOverlay && this.loadingOverlay.style.display !== 'none') {
                this.hideLoading();
            }
        }

        // Enter key for form submission
        if (event.key === 'Enter' && event.target.tagName === 'INPUT') {
            const form = event.target.closest('form');
            if (form) {
                event.preventDefault();
                form.dispatchEvent(new Event('submit'));
            }
        }
    }

    // Utility methods
    showLoading(message = 'Loading...') {
        if (this.loadingOverlay) {
            const messageEl = this.loadingOverlay.querySelector('p');
            if (messageEl) messageEl.textContent = message;
            this.loadingOverlay.style.display = 'flex';
        }
    }

    hideLoading() {
        if (this.loadingOverlay) {
            this.loadingOverlay.style.display = 'none';
        }
    }

    showSuccess(message) {
        if (this.successToast) {
            const messageEl = this.successToast.querySelector('span');
            if (messageEl) messageEl.textContent = message;
            this.successToast.style.display = 'flex';

            setTimeout(() => {
                this.successToast.style.display = 'none';
            }, 5000);
        }
    }

    showError(message) {
        if (this.errorToast) {
            if (this.errorMessage) this.errorMessage.textContent = message;
            this.errorToast.style.display = 'flex';

            setTimeout(() => {
                this.errorToast.style.display = 'none';
            }, 5000);
        }
    }

    // Performance optimization
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Test method to verify endpoint accessibility
    testPhotoEndpoint() {
        console.log('Testing photo upload endpoint...');

        fetch('/profile/photo-test', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            console.log('Test endpoint response status:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('Test endpoint response:', data);
            if (data.success) {
                console.log('✅ Photo upload endpoint is accessible');
                // After confirming endpoint is accessible, refresh photo display
                this.refreshProfilePhotoDisplay();
            } else {
                console.warn('⚠️ Photo upload endpoint test failed');
            }
        })
        .catch(error => {
            console.error('❌ Photo upload endpoint test error:', error);
        });
    }

    refreshProfilePhotoDisplay() {
        console.log('Refreshing profile photo display...');

        // Force refresh of existing photos by adding cache busting
        if (this.currentPhoto && this.currentPhoto.src && !this.currentPhoto.src.includes('data:')) {
            const originalSrc = this.currentPhoto.src.split('?')[0]; // Remove existing cache busting
            const cacheBustedSrc = originalSrc + '?t=' + Date.now();
            this.currentPhoto.src = cacheBustedSrc;
        }

        // Update display state
        this.updatePhotoDisplayState();
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ProfileManager();
});

// Add smooth scrolling for anchor links
document.addEventListener('DOMContentLoaded', () => {
    const anchorLinks = document.querySelectorAll('a[href^="#"]');
    anchorLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            const targetId = link.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);

            if (targetElement) {
                e.preventDefault();
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
});

// Add intersection observer for animations
document.addEventListener('DOMContentLoaded', () => {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
            }
        });
    }, observerOptions);

    // Observe elements for animation
    const animateElements = document.querySelectorAll('.detail-card, .form-section');
    animateElements.forEach(el => {
        observer.observe(el);
    });
});

// Add CSS for animations
const style = document.createElement('style');
style.textContent = `
    .detail-card,
    .form-section {
        opacity: 0;
        transform: translateY(20px);
        transition: opacity 0.6s ease-out, transform 0.6s ease-out;
    }

    .detail-card.animate-in,
    .form-section.animate-in {
        opacity: 1;
        transform: translateY(0);
    }

    .drag-over {
        border: 2px dashed var(--primary-color) !important;
        background: rgba(37, 99, 235, 0.05) !important;
    }

    .form-input.error,
    .form-textarea.error {
        border-color: var(--danger-color) !important;
        box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1) !important;
    }

    .skip-link:focus {
        top: 6px !important;
    }
`;
document.head.appendChild(style);
