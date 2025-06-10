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
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Lapa background
        JArrayList lapaList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LAPA)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Pool background
        JArrayList poolList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.POOL)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Sunset background
        JArrayList sunsetList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.SUNSET)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Lake Pier background
        JArrayList lakePierList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LAKE_PIER)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Lilienstein background
        JArrayList liliensteinList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LILIENSTEIN)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Cinema Lobby background
        JArrayList cinemaLobbyList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.CINEMA_LOBBY)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Shanghai Bund background
        JArrayList shanghaiBundList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.SHANGHAI_BUND)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Studio Garden background
        JArrayList studioGardenList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.STUDIO_GARDEN)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Glass Passage background
        JArrayList glassPassageList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.GLASS_PASSAGE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Artist Workshop background
        JArrayList artistWorkshopList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.ARTIST_WORKSHOP)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // The Sky is on Fire background
        JArrayList skyOnFireList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.THE_SKY_IS_ON_FIRE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Adams Place Bridge background
        JArrayList adamsPlaceBridgeList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.ADAMS_PLACE_BRIDGE)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Pedestrian Overpass background
        JArrayList pedestrianOverpassList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.PEDESTRIAN_OVERPASS)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Voortrekker Interior background
        JArrayList voortrekkerInteriorList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.VOORTREKKER_INTERIOR)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Brown Photostudio 02 background
        JArrayList brownPhotostudioList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.BROWN_PHOTOSTUDIO_02)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Symmetrical Garden 02 background
        JArrayList symmetricalGardenList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.SYMMETRICAL_GARDEN_02)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Little Paris Under Tower background
        JArrayList littleParisList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.LITTLE_PARIS_UNDER_TOWER)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Kloofendal background
        JArrayList kloofendalList = new JArrayList()
                .withParticle(Particle.GRADIENT)
                .withBackground(Background.KLOOFENDAL_48D_PARTLY_CLOUDY_PURESKY)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withSharedEncoder(true)
                .withFPS(1)
                .build();

        // Add elements to all lists
            int i = 2;
            lakeList.add(50 * i++);
            lapaList.add(50 * i++);
            poolList.add(50 * i++);
            sunsetList.add(50 * i++);
            lakePierList.add(50 * i++);
            liliensteinList.add(50 * i++);
            cinemaLobbyList.add(50 * i++);
            shanghaiBundList.add(50 * i++);
            studioGardenList.add(50 * i++);
            glassPassageList.add(50 * i++);
            artistWorkshopList.add(50 * i++);
            skyOnFireList.add(50 * i++);
            adamsPlaceBridgeList.add(50 * i++);
            pedestrianOverpassList.add(50 * i++);
            voortrekkerInteriorList.add(50 * i++);
            brownPhotostudioList.add(50 * i++);
            symmetricalGardenList.add(50 * i++);
            littleParisList.add(50 * i++);
            kloofendalList.add(50 * i++);





    }

}



