# ImageSelector

# Add repository
```gradle
repositories {
    maven { url "https://jitpack.io" }
    maven { url 'https://dl.bintray.com/fubaisum/maven/' }
}
```
# Add dependency
```gradle
        compile 'com.scausum.imageselector:image-selector:0.1.3'
        compile 'com.github.chrisbanes:PhotoView:1.3.0'
        compile 'com.github.bumptech.glide:glide:3.7.0'
```

# USAGE
## launch
```java
        new ImageSelector.Builder()
                .setMultipleChoice(isMultipleChoice)
                .setMaxSelectedSize(maxNum)
                .setCameraEnable(showCamera)
                .setPreviewEnable(showPreview)
//                .setHook(new ImageSelectorHook() {
//                    @Override
//                    public void onImageThumbnailClick(Activity activity, String imagePath) {
//                        //do something
//                    }
//                })
                .build()
                .launch(MainActivity.this, REQUEST_IMAGE_SELECTOR);
```

## callback
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_SELECTOR) {
            ArrayList<String> pathList = data.getStringArrayListExtra(ImageSelector.EXTRA_RESULT_LIST);
            // do something
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
```
# Thanks
[lovetuzitong/MultiImageSelector](https://github.com/lovetuzitong/MultiImageSelector)
