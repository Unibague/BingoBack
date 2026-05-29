package bingo.unibague.demo.services;

import bingo.unibague.demo.models.EnglishBingoCarton;
import bingo.unibague.demo.models.EnglishBingoJugador;
import bingo.unibague.demo.models.EnglishBingoSala;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class EnglishBingoService {

    private final Map<String, EnglishBingoSala> salas = new ConcurrentHashMap<>();
    private List<Map<String, Object>> modulos = new ArrayList<>();

    public EnglishBingoService() {
        cargarPalabras();
    }

    private void cargarPalabras() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("static/palabrasingles.json");
            JsonNode root = mapper.readTree(resource.getInputStream());
            JsonNode modulosNode = root.get("modules");
            for (JsonNode m : modulosNode) {
                Map<String, Object> mod = new HashMap<>();
                mod.put("module", m.get("module").asInt());
                mod.put("topic", m.get("topic").asText());
                List<String> words = new ArrayList<>();
                for (JsonNode w : m.get("words")) words.add(w.asText());
                mod.put("words", words);
                modulos.add(mod);
            }
        } catch (IOException e) {
            System.err.println("Error cargando palabras: " + e.getMessage());
        }
    }

    public List<EnglishBingoSala> getSalasActivas() {
        return salas.values().stream()
                .filter(s -> s.getEstado() != EnglishBingoSala.Estado.FINALIZADA)
                .toList();
    }

    public EnglishBingoSala crearSala(String nombre, String adminId, String adminUsername,
                                       int moduloSeleccionado, int cartonesPerJugador) {
        EnglishBingoSala sala = new EnglishBingoSala();
        sala.setId("sala_" + System.currentTimeMillis());
        sala.setNombre(nombre);
        sala.setAdminId(adminId);
        sala.setAdminUsername(adminUsername);
        sala.setModuloSeleccionado(moduloSeleccionado);
        sala.setCartonesPerJugador(cartonesPerJugador);
        salas.put(sala.getId(), sala);
        return sala;
    }

    public EnglishBingoSala unirseASala(String salaId, String jugadorId, String jugadorUsername) {
        EnglishBingoSala sala = salas.get(salaId);
        if (sala == null || sala.getEstado() == EnglishBingoSala.Estado.FINALIZADA) return null;

        // Si ya está, retornar sala
        boolean yaEsta = sala.getJugadores().stream().anyMatch(j -> j.getId().equals(jugadorId));
        if (yaEsta) return sala;

        // No puede unirse si ya empezó
        if (sala.getEstado() == EnglishBingoSala.Estado.EN_JUEGO) return null;

        EnglishBingoJugador jugador = new EnglishBingoJugador();
        jugador.setId(jugadorId);
        jugador.setUsername(jugadorUsername);

        // El admin entra como moderador, sin cartones
        if (!jugadorId.equals(sala.getAdminId())) {
            List<String> palabras = getPalabrasModulo(sala.getModuloSeleccionado());

            // Recolectar claves de cartones ya existentes para garantizar unicidad
            Set<String> usedKeys = new HashSet<>();
            for (EnglishBingoJugador j : sala.getJugadores()) {
                for (EnglishBingoCarton c : j.getCartones()) {
                    usedKeys.add(claveCarton(c));
                }
            }

            List<EnglishBingoCarton> cartones = new ArrayList<>();
            for (int i = 0; i < sala.getCartonesPerJugador(); i++) {
                cartones.add(generarCartonUnico(palabras, usedKeys));
            }
            jugador.setCartones(cartones);
        }

        sala.getJugadores().add(jugador);
        return sala;
    }

    public EnglishBingoSala iniciarJuego(String salaId) {
        EnglishBingoSala sala = salas.get(salaId);
        if (sala == null) return null;
        sala.setEstado(EnglishBingoSala.Estado.EN_JUEGO);
        return sala;
    }

    public EnglishBingoSala llamarPalabra(String salaId) {
        EnglishBingoSala sala = salas.get(salaId);
        if (sala == null || sala.getEstado() != EnglishBingoSala.Estado.EN_JUEGO) return null;

        List<String> todas = getPalabrasModulo(sala.getModuloSeleccionado());
        List<String> disponibles = todas.stream()
                .filter(p -> !sala.getPalabrasLlamadas().contains(p))
                .toList();
        if (disponibles.isEmpty()) return sala;

        String palabra = disponibles.get(new Random().nextInt(disponibles.size()));
        sala.setPalabraActual(palabra);
        sala.getPalabrasLlamadas().add(palabra);

        // Marcar en cartones y verificar bingo
        for (EnglishBingoJugador jugador : sala.getJugadores()) {
            for (EnglishBingoCarton carton : jugador.getCartones()) {
                marcarPalabra(carton, palabra);
                if (verificarBingo(carton)) {
                    carton.setEsBingo(true);
                    if (sala.getGanador() == null) {
                        sala.setGanador(jugador.getUsername());
                        sala.setEstado(EnglishBingoSala.Estado.FINALIZADA);
                    }
                }
            }
        }
        return sala;
    }

    public EnglishBingoSala finalizarJuego(String salaId, String ganador) {
        EnglishBingoSala sala = salas.get(salaId);
        if (sala == null) return null;
        sala.setEstado(EnglishBingoSala.Estado.FINALIZADA);
        if (ganador != null && !ganador.isEmpty()) sala.setGanador(ganador);
        return sala;
    }

    public EnglishBingoSala getSala(String salaId) {
        return salas.get(salaId);
    }

    @SuppressWarnings("unchecked")
    private List<String> getPalabrasModulo(int modulo) {
        return modulos.stream()
                .filter(m -> ((Integer) m.get("module")).equals(modulo))
                .findFirst()
                .map(m -> (List<String>) m.get("words"))
                .orElse(new ArrayList<>());
    }

    private EnglishBingoCarton generarCarton(List<String> palabras) {
        List<String> shuffled = new ArrayList<>(palabras);
        Collections.shuffle(shuffled);
        shuffled = shuffled.subList(0, Math.min(24, shuffled.size()));

        List<List<String>> grid = new ArrayList<>();
        List<List<Boolean>> marcadas = new ArrayList<>();
        int idx = 0;

        for (int r = 0; r < 5; r++) {
            List<String> fila = new ArrayList<>();
            List<Boolean> filaM = new ArrayList<>();
            for (int c = 0; c < 5; c++) {
                if (r == 2 && c == 2) {
                    fila.add("FREE");
                    filaM.add(true);
                } else {
                    fila.add(idx < shuffled.size() ? shuffled.get(idx++) : "");
                    filaM.add(false);
                }
            }
            grid.add(fila);
            marcadas.add(filaM);
        }

        EnglishBingoCarton carton = new EnglishBingoCarton();
        carton.setId("carton_" + System.currentTimeMillis() + "_" + new Random().nextInt(9999));
        carton.setPalabras(grid);
        carton.setMarcadas(marcadas);
        carton.setEsBingo(false);
        return carton;
    }

    private EnglishBingoCarton generarCartonUnico(List<String> palabras, Set<String> usedKeys) {
        for (int intento = 0; intento < 100; intento++) {
            EnglishBingoCarton carton = generarCarton(palabras);
            String clave = claveCarton(carton);
            if (!usedKeys.contains(clave)) {
                usedKeys.add(clave);
                return carton;
            }
        }
        return generarCarton(palabras);
    }

    private String claveCarton(EnglishBingoCarton carton) {
        return carton.getPalabras().stream()
                .flatMap(List::stream)
                .collect(Collectors.joining(","));
    }

    private void marcarPalabra(EnglishBingoCarton carton, String palabra) {
        List<List<String>> grid = carton.getPalabras();
        List<List<Boolean>> marcadas = carton.getMarcadas();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (palabra.equals(grid.get(r).get(c))) {
                    marcadas.get(r).set(c, true);
                }
            }
        }
    }

    private boolean verificarBingo(EnglishBingoCarton carton) {
        List<List<Boolean>> m = carton.getMarcadas();
        for (int r = 0; r < 5; r++) {
            if (m.get(r).stream().allMatch(Boolean::booleanValue)) return true;
        }
        for (int c = 0; c < 5; c++) {
            boolean col = true;
            for (int r = 0; r < 5; r++) if (!m.get(r).get(c)) { col = false; break; }
            if (col) return true;
        }
        boolean d1 = true, d2 = true;
        for (int i = 0; i < 5; i++) {
            if (!m.get(i).get(i)) d1 = false;
            if (!m.get(i).get(4 - i)) d2 = false;
        }
        return d1 || d2;
    }
}
