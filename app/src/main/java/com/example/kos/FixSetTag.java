package com.example.kos;

public class FixSetTag { //view.setTag(R.id.KEY, value) return null
    private Object NUM_DAY = null, POSITION_LIST = null;

    public FixSetTag(Object NUM_DAY, Object POSITION_LIST) {
        this.NUM_DAY = NUM_DAY;
        this.POSITION_LIST = POSITION_LIST;
    }

    public FixSetTag() {
    }

    public FixSetTag setPOSITION_LIST(Object POSITION_LIST) {
        return new FixSetTag(NUM_DAY, POSITION_LIST);
    }

    public FixSetTag setNUM_DAY(Object NUM_DAY) {
        return new FixSetTag(NUM_DAY, POSITION_LIST);
    }

    public Object getNUM_DAY() {
        return NUM_DAY;
    }

    public Object getPOSITION_LIST() {
        return POSITION_LIST;
    }
}
