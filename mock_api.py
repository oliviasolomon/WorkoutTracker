"""
Mock API server for testing Workout Tracker frontend without Maven/Spring Boot.
Run with: python mock_api.py
Then open: http://localhost:5000/tracker.html
"""

from flask import Flask, request, jsonify, send_from_directory, send_file
from pathlib import Path
from io import BytesIO
from PIL import Image
import json

app = Flask(__name__)

# In-memory storage
workouts = []
users = {}  # {username: {'password': pwd, 'id': user_id}}
next_id = 1
next_user_id = 1

# Path to static files
STATIC_DIR = Path(__file__).parent / "WorkoutTracker" / "src" / "main" / "resources" / "static"

# ============ Static File Serving ============
@app.route('/')
def index():
    return send_from_directory(STATIC_DIR, 'tracker.html')

@app.route('/<path:filename>')
def static_files(filename):
    """Serve static files (HTML, CSS, JS)"""
    return send_from_directory(STATIC_DIR, filename)

# ============ API Endpoints ============

@app.route('/api/auth/signup', methods=['POST'])
def signup():
    """Create a new user account"""
    global next_user_id
    
    data = request.get_json() or {}
    username = data.get('username', '').strip()
    password = data.get('password', '')
    
    if not username or not password:
        return jsonify({'error': 'Username and password required'}), 400
    
    if username in users:
        print(f"[POST /api/auth/signup] Username '{username}' already exists")
        return jsonify({'error': 'Username already in use'}), 409
    
    user_id = next_user_id
    next_user_id += 1
    users[username] = {'password': password, 'id': user_id}
    print(f"[POST /api/auth/signup] Created user '{username}' with id={user_id}")
    return jsonify({'id': user_id, 'username': username}), 201

@app.route('/api/auth/login', methods=['POST'])
def login():
    """Authenticate user and return user_id"""
    data = request.get_json() or {}
    username = data.get('username', '').strip()
    password = data.get('password', '')
    
    if not username or not password:
        return jsonify({'error': 'Username and password required'}), 400
    
    user = users.get(username)
    if not user or user['password'] != password:
        print(f"[POST /api/auth/login] Login failed for '{username}'")
        return jsonify({'error': 'Invalid username or password'}), 401
    
    print(f"[POST /api/auth/login] User '{username}' logged in (id={user['id']})")
    return jsonify({'id': user['id'], 'username': username}), 200

@app.route('/api/exercises', methods=['GET'])
def exercises():
    """Return list of available exercises"""
    return jsonify(['Bench Press','Squat','Deadlift','Overhead Press','Pull Ups','Barbell Row','Leg Press'])

@app.route('/api/workouts', methods=['GET', 'POST'])
def api_workouts():
    """Get all workouts or create a new one"""
    global next_id
    
    if request.method == 'POST':
        data = request.get_json() or {}
        data['id'] = next_id
        data['createdAt'] = data.get('createdAt', '2025-11-24T00:00:00Z')
        data['favorite'] = data.get('favorite', False)
        next_id += 1
        workouts.append(data)
        print(f"[POST /api/workouts] Created workout: {data}")
        return jsonify(data), 201
    
    # GET
    user_id = request.args.get('user_id')
    print(f"[GET /api/workouts] Fetching workouts for user_id={user_id}")
    return jsonify(workouts), 200

@app.route('/workouts/<int:w_id>/favorite', methods=['POST'])
def toggle_favorite(w_id):
    """Toggle favorite status of a workout"""
    for w in workouts:
        if w.get('id') == w_id:
            w['favorite'] = not w.get('favorite', False)
            print(f"[POST /workouts/{w_id}/favorite] Toggled favorite to {w['favorite']}")
            return '', 204
    print(f"[POST /workouts/{w_id}/favorite] Workout not found")
    return jsonify({'error': 'Workout not found'}), 404

@app.route('/metrics/chart', methods=['GET'])
def chart():
    """Return a placeholder chart image"""
    print("[GET /metrics/chart] Serving placeholder chart")
    # Generate a simple placeholder image
    img = Image.new('RGB', (800, 600), color='#2b2d3b')
    img_io = BytesIO()
    img.save(img_io, 'PNG')
    img_io.seek(0)
    return send_file(img_io, mimetype='image/png')

# ============ Error Handlers ============

@app.errorhandler(404)
def not_found(e):
    return jsonify({'error': 'Not Found'}), 404

@app.errorhandler(500)
def server_error(e):
    return jsonify({'error': 'Internal Server Error'}), 500

if __name__ == '__main__':
    print("=" * 60)
    print("Mock API Server for Workout Tracker")
    print("=" * 60)
    print(f"Static files directory: {STATIC_DIR}")
    print(f"Static dir exists: {STATIC_DIR.exists()}")
    print()
    print("Starting server on http://localhost:5000")
    print("Open in browser: http://localhost:5000/tracker.html")
    print("              or: http://localhost:5000/metrics.html")
    print()
    print("Press Ctrl+C to stop the server")
    print("=" * 60)
    app.run(host='0.0.0.0', port=5000, debug=True)
