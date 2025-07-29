package bingo.unibague.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bingo.unibague.demo.models.Carton;
import bingo.unibague.demo.models.Carton.EstadoCarton;
import bingo.unibague.demo.models.autentication.User;
import bingo.unibague.demo.repository.CartonRepository;
import bingo.unibague.demo.repository.UserRepository;

@Service
public class CartonService {

    @Autowired
    private CartonRepository cartonRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea un nuevo cartón para un usuario
     * @param userId ID del usuario
     * @return Cartón creado
     */
    @Transactional
    public Carton crearCarton(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOpt.get();
        String numeroCarton = generarNumeroCarton();
        
        // Verificar que el número no exista
        while (cartonRepository.existsByNumeroCarton(numeroCarton)) {
            numeroCarton = generarNumeroCarton();
        }

        Carton carton = new Carton(user, numeroCarton);
        return cartonRepository.save(carton);
    }

    /**
     * Activa un cartón si el usuario no ha superado su límite
     * @param cartonId ID del cartón
     * @return true si se activó correctamente
     */
    @Transactional
    public boolean activarCarton(Long cartonId) {
        Optional<Carton> cartonOpt = cartonRepository.findById(cartonId);
        if (cartonOpt.isEmpty()) {
            return false;
        }

        Carton carton = cartonOpt.get();
        User user = carton.getUser();

        // Verificar si el usuario puede activar más cartones
        long cartonesActivos = cartonRepository.countByUserIdAndEstado(user.getId(), EstadoCarton.ACTIVO);
        if (cartonesActivos >= user.getMaxCartones()) {
            throw new RuntimeException("El usuario ha alcanzado el límite máximo de cartones activos (" + user.getMaxCartones() + ")");
        }

        carton.activar();
        cartonRepository.save(carton);
        return true;
    }

    /**
     * Desactiva un cartón
     * @param cartonId ID del cartón
     * @return true si se desactivó correctamente
     */
    @Transactional
    public boolean desactivarCarton(Long cartonId) {
        Optional<Carton> cartonOpt = cartonRepository.findById(cartonId);
        if (cartonOpt.isEmpty()) {
            return false;
        }

        Carton carton = cartonOpt.get();
        carton.desactivar();
        cartonRepository.save(carton);
        return true;
    }

    /**
     * Cambia el estado de un cartón entre activo e inactivo
     * @param cartonId ID del cartón
     * @return true si se cambió correctamente
     */
    @Transactional
    public boolean toggleEstadoCarton(Long cartonId) {
        Optional<Carton> cartonOpt = cartonRepository.findById(cartonId);
        if (cartonOpt.isEmpty()) {
            return false;
        }

        Carton carton = cartonOpt.get();
        
        if (carton.isActivo()) {
            carton.desactivar();
        } else if (carton.isInactivo()) {
            // Verificar límite antes de activar
            User user = carton.getUser();
            long cartonesActivos = cartonRepository.countByUserIdAndEstado(user.getId(), EstadoCarton.ACTIVO);
            if (cartonesActivos >= user.getMaxCartones()) {
                throw new RuntimeException("El usuario ha alcanzado el límite máximo de cartones activos (" + user.getMaxCartones() + ")");
            }
            carton.activar();
        }

        cartonRepository.save(carton);
        return true;
    }

    /**
     * Obtiene todos los cartones de un usuario
     * @param userId ID del usuario
     * @return Lista de cartones
     */
    public List<Carton> getCartonesByUser(Long userId) {
        return cartonRepository.findByUserIdOrderByFechaCreacionDesc(userId);
    }

    /**
     * Obtiene cartones activos de un usuario
     * @param userId ID del usuario
     * @return Lista de cartones activos
     */
    public List<Carton> getCartonesActivos(Long userId) {
        return cartonRepository.findCartonesActivosByUserId(userId);
    }

    /**
     * Obtiene cartones inactivos de un usuario
     * @param userId ID del usuario
     * @return Lista de cartones inactivos
     */
    public List<Carton> getCartonesInactivos(Long userId) {
        return cartonRepository.findCartonesInactivosByUserId(userId);
    }

    /**
     * Cuenta cartones activos de un usuario
     * @param userId ID del usuario
     * @return Número de cartones activos
     */
    public long contarCartonesActivos(Long userId) {
        return cartonRepository.countByUserIdAndEstado(userId, EstadoCarton.ACTIVO);
    }

    /**
     * Verifica si un usuario puede activar más cartones
     * @param userId ID del usuario
     * @return true si puede activar más cartones
     */
    public boolean puedeActivarMasCartones(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        long cartonesActivos = cartonRepository.countByUserIdAndEstado(userId, EstadoCarton.ACTIVO);
        return cartonesActivos < user.getMaxCartones();
    }

    /**
     * Obtiene un cartón por su número
     * @param numeroCarton número del cartón
     * @return Cartón opcional
     */
    public Optional<Carton> getCartonByNumero(String numeroCarton) {
        return cartonRepository.findByNumeroCarton(numeroCarton);
    }

    /**
     * Elimina un cartón
     * @param cartonId ID del cartón
     * @return true si se eliminó correctamente
     */
    @Transactional
    public boolean eliminarCarton(Long cartonId) {
        if (cartonRepository.existsById(cartonId)) {
            cartonRepository.deleteById(cartonId);
            return true;
        }
        return false;
    }

    /**
     * Genera un número único para el cartón
     * @return Número de cartón generado
     */
    private String generarNumeroCarton() {
        // Genera un número de cartón único usando timestamp y UUID
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CARTON-" + timestamp + "-" + uuid;
    }

    /**
     * Marca un cartón como usado (para cuando termine un juego)
     * @param cartonId ID del cartón
     * @return true si se marcó correctamente
     */
    @Transactional
    public boolean marcarCartonComoUsado(Long cartonId) {
        Optional<Carton> cartonOpt = cartonRepository.findById(cartonId);
        if (cartonOpt.isEmpty()) {
            return false;
        }

        Carton carton = cartonOpt.get();
        carton.marcarComoUsado();
        cartonRepository.save(carton);
        return true;
    }

    /**
     * Obtiene estadísticas de cartones para un usuario
     * @param userId ID del usuario
     * @return Mapa con estadísticas
     */
    public CartonStats getEstadisticasCartones(Long userId) {
        List<Carton> todosLosCartones = cartonRepository.findByUserId(userId);
        
        long activos = todosLosCartones.stream().filter(Carton::isActivo).count();
        long inactivos = todosLosCartones.stream().filter(Carton::isInactivo).count();
        long usados = todosLosCartones.stream().filter(c -> c.getEstado() == EstadoCarton.USADO).count();
        long bloqueados = todosLosCartones.stream().filter(c -> c.getEstado() == EstadoCarton.BLOQUEADO).count();
        
        Optional<User> userOpt = userRepository.findById(userId);
        int maxCartones = userOpt.map(User::getMaxCartones).orElse(0);
        
        return new CartonStats(activos, inactivos, usados, bloqueados, todosLosCartones.size(), maxCartones);
    }

    // Clase interna para estadísticas
    public static class CartonStats {
        private final long activos;
        private final long inactivos;
        private final long usados;
        private final long bloqueados;
        private final long total;
        private final int maxCartones;

        public CartonStats(long activos, long inactivos, long usados, long bloqueados, long total, int maxCartones) {
            this.activos = activos;
            this.inactivos = inactivos;
            this.usados = usados;
            this.bloqueados = bloqueados;
            this.total = total;
            this.maxCartones = maxCartones;
        }

        // Getters
        public long getActivos() { return activos; }
        public long getInactivos() { return inactivos; }
        public long getUsados() { return usados; }
        public long getBloqueados() { return bloqueados; }
        public long getTotal() { return total; }
        public int getMaxCartones() { return maxCartones; }
        public long getDisponiblesParaActivar() { return maxCartones - activos; }
    }
}