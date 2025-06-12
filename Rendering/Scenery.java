package Rendering;

import java.io.InputStream;

public enum Scenery {
    LAKE("Resources/lake.jpg"),
    LAPA("Resources/lapa.jpg"),
    POOL("Resources/pool.jpg"),
    SUNSET("Resources/sunset.jpg"),
    LAKE_PIER("Resources/lake_pier.jpg"),
    LILIENSTEIN("Resources/lilienstein.jpg"),
    CINEMA_LOBBY("Resources/cinema_lobby.jpg"),
    SHANGHAI_BUND("Resources/shanghai_bund.jpg"),
    STUDIO_GARDEN("Resources/studio_garden.jpg"),
    GLASS_PASSAGE("Resources/glass_passage.jpg"),
    ARTIST_WORKSHOP("Resources/artist_workshop.jpg"),
    THE_SKY_IS_ON_FIRE("Resources/the_sky_is_on_fire.jpg"),
    ADAMS_PLACE_BRIDGE("Resources/adams_place_bridge.jpg"),
    PEDESTRIAN_OVERPASS("Resources/pedestrian_overpass.jpg"),
    BROWN_PHOTOSTUDIO_02("Resources/brown_photostudio_02.jpg"),
    SYMMETRICAL_GARDEN_02("Resources/symmetrical_garden_02.jpg"),
    LITTLE_PARIS_UNDER_TOWER("Resources/little_paris_under_tower.jpg"),
    KLOOFENDAL_48D_PARTLY_CLOUDY_PURESKY("Resources/kloofendal_48d_partly_cloudy_puresky.jpg");

    private final String path;

    Scenery(String path) {
        this.path = path;
    }

    public static InputStream getStream(String path) {
        InputStream in = Scenery.class.getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IllegalStateException("Could not find resource: " + path);
        }
        return in;
    }


    public String toString() {
        return path;
    }
}






