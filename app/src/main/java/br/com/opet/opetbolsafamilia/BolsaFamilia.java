package br.com.opet.opetbolsafamilia;

public class BolsaFamilia {

    private String municipioName, estadoSigla, estado;
    private int beneficiarios;
    private double totalPago;

    public String getMunicipioName() {
        return municipioName;
    }

    public void setMunicipioName(String municipioName) {
        this.municipioName = municipioName;
    }

    public String getEstadoSigla() {
        return estadoSigla;
    }

    public void setEstadoSigla(String estadoSigla) {
        this.estadoSigla = estadoSigla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(int beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(double totalPago) {
        this.totalPago = totalPago;
    }
}
