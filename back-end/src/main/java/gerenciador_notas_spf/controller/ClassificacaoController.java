package gerenciador_notas_spf.controller;

import gerenciador_notas_spf.exception.ExceptionGeneric;
import gerenciador_notas_spf.model.RelatorioModel;
import gerenciador_notas_spf.model.SalaModel;
import gerenciador_notas_spf.service.ApresentacaoService;
import gerenciador_notas_spf.service.RelatorioService;
import gerenciador_notas_spf.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classificacao")
@RequiredArgsConstructor
public class ClassificacaoController {
    private final RelatorioService relatorioService;
    private final ApresentacaoService apresentacaoService;
    private final SalaService salaService;

    @GetMapping("")
    public ResponseEntity<List<RelatorioModel>> listAll() {
        List<RelatorioModel> relatorios = new ArrayList<>();

        try{
            salaService.listAll().stream().map(SalaModel::getId).forEach(
                    (sala) -> relatorios.add(this.getRelatorio(sala))
            );
        } catch (RuntimeException e){
            throw new ExceptionGeneric("INFORMACOES INSUFICIENTES", "APRESENTACAO NOT FOUND, INFORMACOES INSUFICIENTES", HttpStatus.NO_CONTENT.value());
        }

        relatorios.sort(Comparator.comparing(RelatorioModel::getNota));
        return ResponseEntity.status(HttpStatus.OK).body(relatorios);
    }

    private RelatorioModel getRelatorio(UUID salaId) {
        return relatorioService.getRelatorio(
                salaService.findById(salaId),
                apresentacaoService.listAllBySala(salaId)
        );
    }
}