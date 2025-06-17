package com.example.a1210733_1211088_courseproject.utils;

/**
 * Utility class for generating real estate photo URLs
 * Provides various options for online real estate photos
 */
public class RealEstatePhotoProvider {
    
    /**
     * Get a random apartment photo URL
     */
    public static String getApartmentPhoto(int index) {
        String[] apartmentPhotos = {
            "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Modern apartment
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Penthouse
            "https://images.unsplash.com/photo-1556912173-3bb406ef7e77?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Studio apartment
            "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Family apartment
            "https://images.unsplash.com/photo-1554995207-c18c203602cb?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Luxury apartment
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Compact apartment
            "https://images.unsplash.com/photo-1515263487990-61b07816b805?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"  // Seaview apartment
        };
        return apartmentPhotos[index % apartmentPhotos.length];
    }
    
    /**
     * Get a random villa photo URL
     */
    public static String getVillaPhoto(int index) {
        String[] villaPhotos = {
            "https://images.unsplash.com/photo-1505843513577-22bb7d21e455?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Luxury villa
            "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Beachside villa
            "https://images.unsplash.com/photo-1605276374104-dee2a0ed3cd6?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Country villa
            "https://images.unsplash.com/photo-1613977257363-707ba9348227?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Mountain villa
            "https://images.unsplash.com/photo-1600047509807-ba8f99d2cdde?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Duplex villa
            "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Resort villa
            "https://images.unsplash.com/photo-1570129477492-45c003edd2be?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"  // Ranch villa
        };
        return villaPhotos[index % villaPhotos.length];
    }
    
    /**
     * Get a random land photo URL
     */
    public static String getLandPhoto(int index) {
        String[] landPhotos = {
            "https://images.unsplash.com/photo-1500382017468-9049fed747ef?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Residential land
            "https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Urban land
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Seaside land
            "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Industrial land
            "https://images.unsplash.com/photo-1500382017468-9049fed747ef?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80", // Green land
            "https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80"  // Development land
        };
        return landPhotos[index % landPhotos.length];
    }
    
    /**
     * Get photo URL based on property type and index
     */
    public static String getPhotoByType(String propertyType, int index) {
        switch (propertyType.toLowerCase()) {
            case "apartment":
                return getApartmentPhoto(index);
            case "villa":
                return getVillaPhoto(index);
            case "land":
                return getLandPhoto(index);
            default:
                return getApartmentPhoto(index); // Default fallback
        }
    }
    
    /**
     * Alternative photo providers (in case you want different sources)
     */
    
    /**
     * Lorem Picsum - Random photos (faster but less specific)
     */
    public static String getLoremPicsumPhoto(int width, int height, int seed) {
        return "https://picsum.photos/" + width + "/" + height + "?random=" + seed;
    }
    
    /**
     * Unsplash Source API - Category-based photos
     */
    public static String getUnsplashCategoryPhoto(String category, int width, int height) {
        return "https://source.unsplash.com/" + width + "x" + height + "/?" + category;
    }
    
    // Example usage:
    // getUnsplashCategoryPhoto("house", 800, 600)
    // getUnsplashCategoryPhoto("apartment", 800, 600)
    // getUnsplashCategoryPhoto("villa", 800, 600)
}
