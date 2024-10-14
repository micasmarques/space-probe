package com.elo7.space_probe.ui.probes;

import java.util.List;
import java.util.Optional;

import com.elo7.space_probe.app.planets.FindPlanetService;
import com.elo7.space_probe.app.probes.CreateProbeService;
import com.elo7.space_probe.app.probes.FindAllProbeService;
import com.elo7.space_probe.app.probes.FindProbeService;
import com.elo7.space_probe.app.probes.MoveProbeService;
import com.elo7.space_probe.domain.Planet;
import com.elo7.space_probe.domain.Probe;
import com.elo7.space_probe.exceptions.PlanetaNaoEncontradoException;
import com.elo7.space_probe.exceptions.SondaNaoEncontradaException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/probes")
class ProbeController {

    private final CreateProbeService createProbeService;
    private final FindProbeService findProbeService;
    private final FindPlanetService findPlanetService;
    private final FindAllProbeService findAllProbeService;
    private final ProbeCreateDTOToModelConverter probeCreateDTOToModelConverter;
    private final ProbeToDtoConverter probeToDtoConverter;
    private final MoveProbeService moveProbeService;


    ProbeController(CreateProbeService createProbeService, FindProbeService findProbeService, FindPlanetService findPlanetService,
                    FindAllProbeService findAllProbeService, ProbeCreateDTOToModelConverter probeCreateDTOToModelConverter,
                    ProbeToDtoConverter probeToDtoConverter, MoveProbeService moveProbeService) {
        this.createProbeService = createProbeService;
        this.findProbeService = findProbeService;
        this.findPlanetService = findPlanetService;
        this.findAllProbeService = findAllProbeService;
        this.probeCreateDTOToModelConverter = probeCreateDTOToModelConverter;
        this.probeToDtoConverter = probeToDtoConverter;
        this.moveProbeService = moveProbeService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<ProbeDTO> findAll() {
        List<Probe> probes = findAllProbeService.execute();
        return probes.stream().map(probeToDtoConverter::convert).toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    ProbeDTO findById(@PathVariable("id") Integer id) {
        Optional<Probe> probe = findProbeService.execute(id);
        return probe.map(probeToDtoConverter::convert).orElse(null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ProbeDTO create(@RequestBody ProbeCreateDTO probeCreateDTO) {
        Optional<Planet> planet = findPlanetService.execute(probeCreateDTO.planetId());
        Probe probe = probeCreateDTOToModelConverter.convert(
                probeCreateDTO,
                planet.orElseThrow(RuntimeException::new)
        );
        Probe createdProbe = createProbeService.execute(probe);
        return probeToDtoConverter.convert(createdProbe);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/move")
    public ProbeDTO moveProbe(@PathVariable("id") Integer id, @RequestBody MoveProbeRequest moveProbeRequest) {
        Probe probe = findProbeService.execute(id).orElseThrow(() ->
                new SondaNaoEncontradaException("Sonda não encontrada com ID: " + id));
        Planet planet = findPlanetService.execute(probe.getPlanetId()).orElseThrow(() ->
                new PlanetaNaoEncontradoException("Planeta não encontrado para ID: " + probe.getPlanetId()));

        Probe movedProbe = moveProbeService.execute(probe, planet, moveProbeRequest.getCommands(), moveProbeRequest.getInitialDirection());
        return new ProbeToDtoConverter().convert(movedProbe);
    }
}
