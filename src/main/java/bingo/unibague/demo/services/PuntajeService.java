package bingo.unibague.demo.services;

import bingo.unibague.demo.models.Puntaje;
import bingo.unibague.demo.payload.response.EstadisticasResponse;
import bingo.unibague.demo.repository.PuntajeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PuntajeService {
    
    @Autowired
    private PuntajeRepository puntajeRepository;
    
    public Puntaje guardarPuntaje(Puntaje puntaje, String userEmail) {
        puntaje.setUserEmail(userEmail);
        return puntajeRepository.save(puntaje);
    }
    
    public EstadisticasResponse obtenerEstadisticasUsuario(String userEmail) {
        long juegosJugados = puntajeRepository.countByUserEmail(userEmail);
        long juegosGanados = puntajeRepository.countByUserEmailAndEsGanadorTrue(userEmail);
        Integer puntosTotales = puntajeRepository.sumPuntajesByUserEmail(userEmail);
        
        EstadisticasResponse stats = new EstadisticasResponse();
        stats.setJuegosJugados((int) juegosJugados);
        stats.setJuegosGanados((int) juegosGanados);
        stats.setPuntosTotales(puntosTotales != null ? puntosTotales : 0);
        stats.setRachaActual(0); // Implementar lógica de racha si es necesario
        stats.setPorcentajeVictorias(juegosJugados > 0 ? 
            (double) juegosGanados / juegosJugados * 100 : 0.0);
        
        return stats;
    }
    
    public List<Puntaje> obtenerRanking() {
        return puntajeRepository.findTopPuntajes();
    }
    
    public List<Puntaje> obtenerHistorialUsuario(String userEmail) {
        return puntajeRepository.findByUserEmailOrderByFechaJuegoDesc(userEmail);
    }
}
