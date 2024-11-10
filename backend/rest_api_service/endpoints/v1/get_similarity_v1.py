"""
get similarity v1 is based on the trained model
model trained on 
"""

import kagglehub
import os

import numpy as np
import tensorflow as tf

from http import HTTPStatus
from PIL import Image
from shutil import copyfile

from ...utils.utils import error_handling
from ...config.config import Config

# SET DATASET

conf = Config()

TRAIED_MODELS_PATH = conf.trained_models_path
MODEL_NAME = conf.model_name
rows, cols = 160, 160

# GET DATASET

path = kagglehub.dataset_download("hereisburak/pins-face-recognition")

# PROCESS DATASET

work_dir = path

TRAINED_MODELS_PATH = os.path.join(work_dir, "trained_models")
TEST_IMAGES_PATH = os.path.join(work_dir, "test_images")

train_dir = os.path.join(work_dir, 'train')
test_dir = os.path.join(work_dir, 'test')
os.makedirs(train_dir, exist_ok=True)
os.makedirs(test_dir, exist_ok=True)

train_ratio = 0.8
test_ratio = 0.2

dir_list = os.listdir(os.path.join(work_dir ,'105_classes_pins_dataset'))

print('The Number of Classes in the Dataset is:{}'.format(len(dir_list)))

source_dir = os.path.join(work_dir ,'105_classes_pins_dataset')

dir_list = os.listdir(source_dir)

for folder in dir_list:
    data_dir = os.listdir(os.path.join(source_dir,folder))
    np.random.shuffle(data_dir)

    os.makedirs(os.path.join(train_dir, folder), exist_ok=True)
    os.makedirs(os.path.join(test_dir, folder), exist_ok=True)

    train_data = data_dir[:int(len(data_dir)*train_ratio+1)]
    test_data = data_dir[-int(len(data_dir)*test_ratio):]

    for image in train_data:
        copyfile(os.path.join(source_dir,folder,image) , os.path.join(train_dir,folder,image))

    for image in test_data:
        copyfile(os.path.join(source_dir,folder,image) , os.path.join(test_dir,folder,image))

# PREPARE

train_datagen: tf.keras.preprocessing.image.ImageDataGenerator = \
    tf.keras.preprocessing.image.ImageDataGenerator(
    rescale=1/255,
    shear_range=0.2,
    zoom_range=0.2,
    horizontal_flip=True,
    rotation_range=40,
    width_shift_range=0.1,
    height_shift_range=0.1)
test_datagen: tf.keras.preprocessing.image.ImageDataGenerator = \
    tf.keras.preprocessing.image.ImageDataGenerator(rescale=1/255)

train_generator=train_datagen.flow_from_directory(
    train_dir,
    target_size=(rows,cols),
    class_mode='categorical')

model = tf.keras.models.load_model(f"{TRAIED_MODELS_PATH}{MODEL_NAME}")
classes: dict = train_generator.class_indices
class_names: list = list(classes.keys())


def recognize(_file_stream) -> tuple[dict, HTTPStatus]:
    """
    returns the person lookalike probability as map <Person>: probability
    Request:
        curl --location --request GET 'http://127.0.0.1:5000/v1/similarity' \
        --form 'file=@"<path to file>"'
    Response:
        {
            "Barack Obama": 0.06299323588609695,
            "Elon Musk": 0.04285495728254318
        }
    """
    img = None
    try:
        img = Image.open(_file_stream)
        img = img.resize((rows, cols))

        img_array = tf.keras.preprocessing.image.img_to_array(img)
        img_array = tf.expand_dims(img_array, 0)
        img_array = img_array/255.

        score: np.ndarray = model.predict(img_array)
    except:
        err_resp = error_handling(
            "Unsupported media type",
            HTTPStatus.UNSUPPORTED_MEDIA_TYPE
        )
        return err_resp, HTTPStatus.UNSUPPORTED_MEDIA_TYPE

    print(
        f"The predicted twin is {class_names[np.argmax(score)][5:].title()}.")

    probabilities = [(_sc, class_names[idx][5:].title()) for idx, _sc in enumerate(score[0])]
    probabilities = sorted(probabilities, key=lambda x: x[0], reverse=True)

    return {_key:float(_val) for _val, _key in probabilities[:5]}, HTTPStatus.OK
