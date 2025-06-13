package com.vn.capstone.domain.response.shipper;

public class ShipperStatsResponse {
    private long delivered;
    private long failed;
    private long inProgress;
    private long codAmount;

    public ShipperStatsResponse() {

    }

    public ShipperStatsResponse(long delivered, long failed, long inProgress, long codAmount) {
        this.delivered = delivered;
        this.failed = failed;
        this.inProgress = inProgress;
        this.codAmount = codAmount;
    }

    public long getDelivered() {
        return delivered;
    }

    public void setDelivered(long delivered) {
        this.delivered = delivered;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getCodAmount() {
        return codAmount;
    }

    public void setCodAmount(long codAmount) {
        this.codAmount = codAmount;
    }

    // Getters & Setters
}
