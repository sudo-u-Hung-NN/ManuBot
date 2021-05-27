public interface Timed {

    /**
     * This function indicates whether an object is going
     * to do a specific timed-action
     * @param timeNow: network time
     * @return true: do the work, false: keep waiting
     */
    public boolean activate(double timeNow);

    public void setActivateTime(double timeNow);
}
