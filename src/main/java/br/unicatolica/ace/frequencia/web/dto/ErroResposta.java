package br.unicatolica.ace.frequencia.web.dto;

import java.time.OffsetDateTime;
import java.util.Map;

/** Corpo padrão de erro da API: {timestamp, status, erro, mensagem, campos}. */
public record ErroResposta(OffsetDateTime timestamp, int status, String erro, String mensagem,
                           Map<String, String> campos) {
}
