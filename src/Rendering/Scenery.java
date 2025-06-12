package Rendering;

import java.io.InputStream;

public enum Scenery {
    LAKE("resources/lake.jpg"),
    LAPA("resources/lapa.jpg"),
    POOL("resources/pool.jpg"),
    SUNSET("resources/sunset.jpg"),
    LAKE_PIER("resources/lake_pier.jpg"),
    LILIENSTEIN("resources/lilienstein.jpg"),
    CINEMA_LOBBY("resources/cinema_lobby.jpg"),
    SHANGHAI_BUND("resources/shanghai_bund.jpg"),
    STUDIO_GARDEN("resources/studio_garden.jpg"),
    GLASS_PASSAGE("resources/glass_passage.jpg"),
    ARTIST_WORKSHOP("resources/artist_workshop.jpg"),
    THE_SKY_IS_ON_FIRE("resources/the_sky_is_on_fire.jpg"),
    ADAMS_PLACE_BRIDGE("resources/adams_place_bridge.jpg"),
    PEDESTRIAN_OVERPASS("resources/pedestrian_overpass.jpg"),
    BROWN_PHOTOSTUDIO_02("resources/brown_photostudio_02.jpg"),
    SYMMETRICAL_GARDEN_02("resources/symmetrical_garden_02.jpg"),
    LITTLE_PARIS_UNDER_TOWER("resources/little_paris_under_tower.jpg"),
    KLOOFENDAL_48D_PARTLY_CLOUDY_PURESKY("resources/kloofendal_48d_partly_cloudy_puresky.jpg");

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






