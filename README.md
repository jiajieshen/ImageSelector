# ImageSelector

# USAGE
## launch
```java
        new ImageSelector.Builder()
                .setMultipleChoiceMode(isMultipleChoiceMode)
                .setMaxSelectableSize(maxNum)
                .setShowCamera(showCamera)
                .build()
                .launchForActivityCallback(MainActivity.this, REQUEST_IMAGE_SELECTOR);
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
### or
```java
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectComplete(SelectCompleteEvent event) {
        ArrayList<String> pathList = event.getSelectedPathList();
        // do something
    }
```
