package br.unicatolica.ace.frequencia.web;

import br.unicatolica.ace.frequencia.application.exception.ConflitoException;
import br.unicatolica.ace.frequencia.application.exception.RecursoNaoEncontradoException;
import br.unicatolica.ace.frequencia.domain.exception.DadoInvalidoException;
import br.unicatolica.ace.frequencia.domain.exception.RegraNegocioException;
import br.unicatolica.ace.frequencia.web.dto.ErroResposta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduz exceções em respostas HTTP com corpo padrão {timestamp, status, erro, mensagem, campos}.
 * 400 validação · 404 não encontrado · 409 duplicado · 422 regra de negócio.
 */
@RestControllerAdvice
public class ErroHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ErroHandler.class);

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regra(RegraNegocioException e) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), Map.of("regra", e.getRegra()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage(), Map.of());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> conflito(ConflitoException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage(), Map.of(e.getCampo(), e.getMessage()));
    }

    @ExceptionHandler(DadoInvalidoException.class)
    public ResponseEntity<ErroResposta> dadoInvalido(DadoInvalidoException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage(), Map.of(e.getCampo(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> campos.putIfAbsent(f.getField(), f.getDefaultMessage()));
        return resposta(HttpStatus.BAD_REQUEST, "Verifique os campos informados.", campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErroResposta> requisicaoInvalida(Exception e) {
        String mensagem = switch (e) {
            case MethodArgumentTypeMismatchException m ->
                    "Valor inválido para '%s': %s".formatted(m.getName(), m.getValue());
            case MissingServletRequestParameterException m -> "Informe o parâmetro '%s'.".formatted(m.getParameterName());
            default -> "Corpo da requisição inválido. Confira o JSON (datas yyyy-MM-dd, horas HH:mm, valores dos enums).";
        };
        return resposta(HttpStatus.BAD_REQUEST, mensagem, Map.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> rotaInexistente(NoResourceFoundException e) {
        return resposta(HttpStatus.NOT_FOUND, "Endereço não encontrado: /" + e.getResourcePath(), Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> inesperado(Exception e) {
        if (e instanceof ErrorResponse erroDoSpring) { // ex.: 405 método não permitido, 415 tipo de conteúdo
            HttpStatus status = HttpStatus.valueOf(erroDoSpring.getStatusCode().value());
            return resposta(status, e.getMessage(), Map.of());
        }
        LOG.error("Erro inesperado", e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado no servidor. Tente novamente.", Map.of());
    }

    private static ResponseEntity<ErroResposta> resposta(HttpStatus status, String mensagem, Map<String, String> campos) {
        return ResponseEntity.status(status)
                .body(new ErroResposta(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), mensagem, campos));
    }
}
