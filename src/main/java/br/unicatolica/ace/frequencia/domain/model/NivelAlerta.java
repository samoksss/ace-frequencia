package br.unicatolica.ace.frequencia.domain.model;

/**
 * Nível do alerta de faltoso (RN09).
 * ATENCAO: mandar lembrete ("semana que vem tem aula"). CRITICO: perguntar se deseja retornar ou cancelar.
 */
public enum NivelAlerta {
    NENHUM, ATENCAO, CRITICO
}
