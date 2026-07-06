import hashlib, secrets, base64
password = 'Admin123!'
salt = secrets.token_hex(12)
iterations = 1000000
dk = hashlib.pbkdf2_hmac('sha256', password.encode(), salt.encode(), iterations)
hash_val = base64.b64encode(dk).decode().strip()
print(f'pbkdf2_sha256${iterations}${salt}${hash_val}')
