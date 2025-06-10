import Collections.*;
import Rendering.*;

public class Main {

    public static void main(String[] args) {

        // Lake background
        JArrayList lakeList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LAKE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Lapa background
        JArrayList lapaList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LAPA)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Pool background
        JArrayList poolList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.POOL)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Sunset background
        JArrayList sunsetList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.SUNSET)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Lake Pier background
        JArrayList lakePierList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LAKE_PIER)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Lilienstein background
        JArrayList liliensteinList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LILIENSTEIN)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Cinema Lobby background
        JArrayList cinemaLobbyList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.CINEMA_LOBBY)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Shanghai Bund background
        JArrayList shanghaiBundList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.SHANGHAI_BUND)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Studio Garden background
        JArrayList studioGardenList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.STUDIO_GARDEN)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Glass Passage background
        JArrayList glassPassageList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.GLASS_PASSAGE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Artist Workshop background
        JArrayList artistWorkshopList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.ARTIST_WORKSHOP)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // The Sky is on Fire background
        JArrayList skyOnFireList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.THE_SKY_IS_ON_FIRE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Adams Place Bridge background
        JArrayList adamsPlaceBridgeList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.ADAMS_PLACE_BRIDGE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Pedestrian Overpass background
        JArrayList pedestrianOverpassList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.PEDESTRIAN_OVERPASS)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Voortrekker Interior background
        JArrayList voortrekkerInteriorList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.VOORTREKKER_INTERIOR)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Brown Photostudio 02 background
        JArrayList brownPhotostudioList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.BROWN_PHOTOSTUDIO_02)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Symmetrical Garden 02 background
        JArrayList symmetricalGardenList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.SYMMETRICAL_GARDEN_02)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Little Paris Under Tower background
        JArrayList littleParisList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LITTLE_PARIS_UNDER_TOWER)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Kloofendal background
        JArrayList kloofendalList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.KLOOFENDAL_48D_PARTLY_CLOUDY_PURESKY)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .build();

        // Add elements to all lists
        for (int i = 0; i < 10; i++) {
            lakeList.add(50);
            lapaList.add(50);
            poolList.add(50);
            sunsetList.add(50);
            lakePierList.add(50);
            liliensteinList.add(50);
            cinemaLobbyList.add(50);
            shanghaiBundList.add(50);
            studioGardenList.add(50);
            glassPassageList.add(50);
            artistWorkshopList.add(50);
            skyOnFireList.add(50);
            adamsPlaceBridgeList.add(50);
            pedestrianOverpassList.add(50);
            voortrekkerInteriorList.add(50);
            brownPhotostudioList.add(50);
            symmetricalGardenList.add(50);
            littleParisList.add(50);
            kloofendalList.add(50);
        }




    }

}



