from flask import Flask, request, send_file, jsonify
import os
from io import BytesIO
from PIL import Image, ExifTags

app = Flask(__name__)

# Folders to save uploaded and processed images
UPLOAD_FOLDER = 'uploads/'
PROCESSED_FOLDER = 'processed/'

# Ensure the folders exist
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(PROCESSED_FOLDER, exist_ok=True)


# Function to rotate the image based on EXIF orientation
def correct_image_orientation(img):
    try:
        # Check for EXIF orientation tag
        for orientation in ExifTags.TAGS.keys():
            if ExifTags.TAGS[orientation] == 'Orientation':
                break
        exif = img._getexif()
        if exif is not None:
            exif_orientation = exif.get(orientation)
            if exif_orientation == 3:
                img = img.rotate(180, expand=True)
            elif exif_orientation == 6:
                img = img.rotate(270, expand=True)
            elif exif_orientation == 8:
                img = img.rotate(90, expand=True)
    except (AttributeError, KeyError, IndexError):
        # In case there's no EXIF or error, do nothing
        pass
    return img


@app.route('/process_photo', methods=['POST'])
def process_photo():
    # Check if an image file is part of the request
    if 'image' not in request.files:
        return jsonify({"error": "No image part in the request"}), 400

    image = request.files['image']

    if image.filename == '':
        return jsonify({"error": "No selected file"}), 400

    # Save the uploaded image
    image_path = os.path.join(UPLOAD_FOLDER, image.filename)
    image.save(image_path)

    # Open the image with PIL
    try:
        img = Image.open(image.stream)
    except Exception as e:
        return jsonify({"error": f"Error opening image: {str(e)}"}), 400

    # Correct the orientation if needed (based on EXIF data)
    img = correct_image_orientation(img)

    # Convert the image to black and white (grayscale)
    img = img.convert("L")  # "L" mode is for grayscale

    # Save the processed image
    processed_image_path = os.path.join(PROCESSED_FOLDER, 'processed_' + image.filename)
    img.save(processed_image_path)

    # Send the processed image back to the client
    return send_file(processed_image_path, mimetype='image/jpeg')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
