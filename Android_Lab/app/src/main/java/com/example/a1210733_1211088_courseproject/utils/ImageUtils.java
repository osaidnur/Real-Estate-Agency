package com.example.a1210733_1211088_courseproject.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import com.example.a1210733_1211088_courseproject.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Utility class for loading images from various sources (web URLs, local files, drawable resources)
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    /**
     * Loads an image from various sources into an ImageView
     * Supports:
     * - Web URLs (http/https)
     * - Local file paths
     * - Drawable resource names
     * 
     * @param context The application context
     * @param imageSource The image source (URL, file path, or resource name)
     * @param imageView The ImageView to load the image into
     * @param placeholderRes Resource ID for placeholder image (use 0 for default)
     */    public static void loadImage(Context context, String imageSource, ImageView imageView, int placeholderRes) {
        int placeholder = placeholderRes != 0 ? placeholderRes : R.drawable.ic_home;
        
        Log.d(TAG, "Loading image from source: " + imageSource);
        
        if (imageSource == null || imageSource.isEmpty()) {
            Log.d(TAG, "Image source is null or empty, using placeholder");
            imageView.setImageResource(placeholder);
            return;
        }

        if (isWebUrl(imageSource)) {
            Log.d(TAG, "Loading web URL: " + imageSource);
            Picasso.get()
                .load(imageSource)
                .placeholder(placeholder)
                .error(placeholder)
                .into(imageView);
        } else if (isLocalFilePath(imageSource)) {
            Log.d(TAG, "Loading local file: " + imageSource);
            File imageFile = new File(imageSource);
            if (imageFile.exists()) {
                Picasso.get()
                    .load(imageFile)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(imageView);
            } else {
                Log.d(TAG, "Local file doesn't exist: " + imageSource);
                imageView.setImageResource(placeholder);
            }
        } else if (isDrawableResourcePath(imageSource)) {
            Log.d(TAG, "Loading drawable resource path: " + imageSource);
            String resourceName = extractResourceNameFromPath(imageSource);
            int resourceId = getDrawableResourceId(context, resourceName);
            if (resourceId != 0) {
                Log.d(TAG, "Found drawable resource: " + resourceName + " with ID: " + resourceId);
                imageView.setImageResource(resourceId);
            } else {
                Log.d(TAG, "Drawable resource not found: " + resourceName);
                imageView.setImageResource(placeholder);
            }
        } else if (isDrawableResource(imageSource)) {
            Log.d(TAG, "Loading drawable resource with prefix: " + imageSource);
            String resourceName = imageSource.substring("drawable:".length());
            int resourceId = getDrawableResourceId(context, resourceName);
            if (resourceId != 0) {
                Log.d(TAG, "Found drawable resource: " + resourceName + " with ID: " + resourceId);
                imageView.setImageResource(resourceId);
            } else {
                Log.d(TAG, "Drawable resource not found: " + resourceName);
                imageView.setImageResource(placeholder);
            }
        } else {
            Log.d(TAG, "Trying as drawable resource name: " + imageSource);
            int resourceId = getDrawableResourceId(context, imageSource);
            if (resourceId != 0) {
                Log.d(TAG, "Found drawable resource: " + imageSource + " with ID: " + resourceId);
                imageView.setImageResource(resourceId);
            } else {
                Log.d(TAG, "Unknown source type, using placeholder: " + imageSource);
                imageView.setImageResource(placeholder);
            }
        }
    }

    /**
     * Convenience method with default placeholder
     */
    public static void loadImage(Context context, String imageSource, ImageView imageView) {
        loadImage(context, imageSource, imageView, R.drawable.ic_home);
    }

    /**
     * Loads property images with consistent styling
     * @param context The application context
     * @param imageSource The image source
     * @param imageView The ImageView to load into
     */
    public static void loadPropertyImage(Context context, String imageSource, ImageView imageView) {
        loadImage(context, imageSource, imageView, R.drawable.ic_home);
    }

    /**
     * Checks if the image source is a web URL
     * @param imageSource The image source string
     * @return true if it's a web URL, false otherwise
     */
    private static boolean isWebUrl(String imageSource) {
        return imageSource.startsWith("http://") || imageSource.startsWith("https://");
    }

    /**
     * Checks if the image source is a local file path
     * @param imageSource The image source string
     * @return true if it's a local file path, false otherwise
     */
    private static boolean isLocalFilePath(String imageSource) {
        return imageSource.startsWith("/") || 
               imageSource.startsWith("file://") || 
               imageSource.contains("storage/") || 
               imageSource.contains("Android/") ||
               imageSource.startsWith("content://"); // Content URIs from gallery
    }    /**
     * Checks if the image source is a drawable resource with prefix
     * @param imageSource The image source string
     * @return true if it's a drawable resource with prefix, false otherwise
     */
    private static boolean isDrawableResource(String imageSource) {
        return imageSource.startsWith("drawable:");
    }

    /**
     * Checks if the image source is a drawable resource path (like Android_Lab/app/src/main/res/drawable/logo.png)
     * @param imageSource The image source string
     * @return true if it's a drawable resource path, false otherwise
     */
    private static boolean isDrawableResourcePath(String imageSource) {
        return imageSource.contains("/res/drawable/") || 
               imageSource.contains("\\res\\drawable\\") ||
               imageSource.contains("drawable/");
    }

    /**
     * Extracts resource name from drawable path
     * For "Android_Lab/app/src/main/res/drawable/logo.png" returns "logo"
     * @param imagePath The drawable resource path
     * @return The resource name without extension
     */
    private static String extractResourceNameFromPath(String imagePath) {
        String filename = imagePath;
        
        // Get the filename part
        if (imagePath.contains("/")) {
            filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        } else if (imagePath.contains("\\")) {
            filename = imagePath.substring(imagePath.lastIndexOf("\\") + 1);
        }
        
        // Remove file extension
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        
        return filename;
    }

    /**
     * Gets drawable resource ID from resource name
     * @param context The application context
     * @param resourceName The name of the drawable resource
     * @return The resource ID, or 0 if not found
     */
    private static int getDrawableResourceId(Context context, String resourceName) {
        try {
            return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        } catch (Exception e) {
            return 0;
        }
    }    /**
     * Example usage patterns for storing image sources in database:
     * 
     * Web URLs:
     * - "https://example.com/images/house1.jpg"
     * - "http://api.realtor.com/photos/12345.png"
     * 
     * Local file paths:
     * - "/storage/emulated/0/Pictures/house1.jpg"
     * - "file:///android_asset/sample_house.jpg"
     * - "content://media/external/images/media/1234"
     * 
     * Drawable resource paths (automatically detected):
     * - "Android_Lab/app/src/main/res/drawable/logo.png" → loads R.drawable.logo
     * - "app/src/main/res/drawable/blog.jpg" → loads R.drawable.blog
     * - "res/drawable/icon.png" → loads R.drawable.icon
     * 
     * Drawable resource names (direct reference):
     * - "logo" → loads R.drawable.logo
     * - "blog" → loads R.drawable.blog
     * - "icon" → loads R.drawable.icon
     * 
     * Drawable resources with prefix:
     * - "drawable:logo" → loads R.drawable.logo
     * - "drawable:blog" → loads R.drawable.blog
     * - "drawable:icon" → loads R.drawable.icon
     * 
     * For your logo.png drawable, you can store in database any of these:
     * - "Android_Lab/app/src/main/res/drawable/logo.png" (full path)
     * - "logo" (resource name only)
     * - "drawable:logo" (with prefix)
     */
}
