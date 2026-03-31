package com.txstate.bloodhound.model;

/**
 * Stores latest and average metric values for dashboard display.
 */
public class DashboardSummary {
    private Integer latestSystolic;
    private Integer latestDiastolic;
    private Integer latestTotalCholesterol;
    private Integer latestHdl;
    private Integer latestLdl;
    private Double latestWeight;
    private Double averageSystolic;
    private Double averageDiastolic;
    private Double averageTotalCholesterol;
    private Double averageHdl;
    private Double averageLdl;
    private Double averageWeight;

    public DashboardSummary() {
    }

    public DashboardSummary(Integer latestSystolic,
                            Integer latestDiastolic,
                            Integer latestTotalCholesterol,
                            Integer latestHdl,
                            Integer latestLdl,
                            Double latestWeight,
                            Double averageSystolic,
                            Double averageDiastolic,
                            Double averageTotalCholesterol,
                            Double averageHdl,
                            Double averageLdl,
                            Double averageWeight) {
        this.latestSystolic = latestSystolic;
        this.latestDiastolic = latestDiastolic;
        this.latestTotalCholesterol = latestTotalCholesterol;
        this.latestHdl = latestHdl;
        this.latestLdl = latestLdl;
        this.latestWeight = latestWeight;
        this.averageSystolic = averageSystolic;
        this.averageDiastolic = averageDiastolic;
        this.averageTotalCholesterol = averageTotalCholesterol;
        this.averageHdl = averageHdl;
        this.averageLdl = averageLdl;
        this.averageWeight = averageWeight;
    }

    public Integer getLatestSystolic() {
        return latestSystolic;
    }

    public void setLatestSystolic(Integer latestSystolic) {
        this.latestSystolic = latestSystolic;
    }

    public Integer getLatestDiastolic() {
        return latestDiastolic;
    }

    public void setLatestDiastolic(Integer latestDiastolic) {
        this.latestDiastolic = latestDiastolic;
    }

    public Integer getLatestTotalCholesterol() {
        return latestTotalCholesterol;
    }

    public void setLatestTotalCholesterol(Integer latestTotalCholesterol) {
        this.latestTotalCholesterol = latestTotalCholesterol;
    }

    public Integer getLatestHdl() {
        return latestHdl;
    }

    public void setLatestHdl(Integer latestHdl) {
        this.latestHdl = latestHdl;
    }

    public Integer getLatestLdl() {
        return latestLdl;
    }

    public void setLatestLdl(Integer latestLdl) {
        this.latestLdl = latestLdl;
    }

    public Double getLatestWeight() {
        return latestWeight;
    }

    public void setLatestWeight(Double latestWeight) {
        this.latestWeight = latestWeight;
    }

    public Double getAverageSystolic() {
        return averageSystolic;
    }

    public void setAverageSystolic(Double averageSystolic) {
        this.averageSystolic = averageSystolic;
    }

    public Double getAverageDiastolic() {
        return averageDiastolic;
    }

    public void setAverageDiastolic(Double averageDiastolic) {
        this.averageDiastolic = averageDiastolic;
    }

    public Double getAverageTotalCholesterol() {
        return averageTotalCholesterol;
    }

    public void setAverageTotalCholesterol(Double averageTotalCholesterol) {
        this.averageTotalCholesterol = averageTotalCholesterol;
    }

    public Double getAverageHdl() {
        return averageHdl;
    }

    public void setAverageHdl(Double averageHdl) {
        this.averageHdl = averageHdl;
    }

    public Double getAverageLdl() {
        return averageLdl;
    }

    public void setAverageLdl(Double averageLdl) {
        this.averageLdl = averageLdl;
    }

    public Double getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(Double averageWeight) {
        this.averageWeight = averageWeight;
    }

    @Override
    public String toString() {
        return "DashboardSummary{"
                + "latestSystolic=" + latestSystolic
                + ", latestDiastolic=" + latestDiastolic
                + ", latestTotalCholesterol=" + latestTotalCholesterol
                + ", latestHdl=" + latestHdl
                + ", latestLdl=" + latestLdl
                + ", latestWeight=" + latestWeight
                + ", averageSystolic=" + averageSystolic
                + ", averageDiastolic=" + averageDiastolic
                + ", averageTotalCholesterol=" + averageTotalCholesterol
                + ", averageHdl=" + averageHdl
                + ", averageLdl=" + averageLdl
                + ", averageWeight=" + averageWeight
                + '}';
    }
}
