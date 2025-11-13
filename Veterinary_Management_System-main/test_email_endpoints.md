# Test Email Endpoints

## Test Your Email Configuration

### 1. Check Email Configuration
Visit: `http://localhost:7055/api/email-test/config`

This will show:
- From email address
- Whether password is set
- MailSender status

### 2. Check Email Service Status
Visit: `http://localhost:7055/api/email-test/status`

This will confirm if the email service is ready.

### 3. Send Test Email
Use this URL to send a test email to yourself:
`http://localhost:7055/api/email-test/send?to=liyakhath0409@gmail.com`

## Expected Results

If everything is working:
- Configuration should show your Gmail address
- Status should show "Email service is configured and ready"
- Test email should be sent successfully

## Troubleshooting

If you get errors:
1. Check your Gmail App Password in `application.properties`
2. Ensure 2-Factor Authentication is enabled on Gmail
3. Verify the Gmail username is correct
4. Check application logs for detailed error messages
