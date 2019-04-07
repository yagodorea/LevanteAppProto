package com.dorea.denuncia.utils;

public class Occurrence {

    String contexto;
    String forca;
    String imgRef;
    Double lat;
    Double lon;
    String timestamp;
    Boolean violenciaAbusiva;
    Boolean impedimentoGravacao;
    Boolean agenteSemIdentificacao;
    Boolean usoIndevidoArmaDeFogo;
    Boolean abusoAutoridade;
    Boolean humilhacaoTortura;
    Boolean subornoExtorsao;

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getForca() {
        return forca;
    }

    public void setForca(String forca) {
        this.forca = forca;
    }

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getViolenciaAbusiva() {
        return violenciaAbusiva;
    }

    public void setViolenciaAbusiva(Boolean violenciaAbusiva) {
        this.violenciaAbusiva = violenciaAbusiva;
    }

    public Boolean getImpedimentoGravacao() {
        return impedimentoGravacao;
    }

    public void setImpedimentoGravacao(Boolean impedimentoGravacao) {
        this.impedimentoGravacao = impedimentoGravacao;
    }

    public Boolean getAgenteSemIdentificacao() {
        return agenteSemIdentificacao;
    }

    public void setAgenteSemIdentificacao(Boolean agenteSemIdentificacao) {
        this.agenteSemIdentificacao = agenteSemIdentificacao;
    }

    public Boolean getUsoIndevidoArmaDeFogo() {
        return usoIndevidoArmaDeFogo;
    }

    public void setUsoIndevidoArmaDeFogo(Boolean usoIndevidoArmaDeFogo) {
        this.usoIndevidoArmaDeFogo = usoIndevidoArmaDeFogo;
    }

    public Boolean getAbusoAutoridade() {
        return abusoAutoridade;
    }

    public void setAbusoAutoridade(Boolean abusoAutoridade) {
        this.abusoAutoridade = abusoAutoridade;
    }

    public Boolean getHumilhacaoTortura() {
        return humilhacaoTortura;
    }

    public void setHumilhacaoTortura(Boolean humilhacaoTortura) {
        this.humilhacaoTortura = humilhacaoTortura;
    }

    public Boolean getSubornoExtorsao() {
        return subornoExtorsao;
    }

    public void setSubornoExtorsao(Boolean subornoExtorsao) {
        this.subornoExtorsao = subornoExtorsao;
    }
}
