package swarm.swarmcomposer.activity;

import android.content.Context;

class Main {

    private Context context;

    /**
     * Main initializes the Appinstance so it ready to go if any other class needs it
     * @param context Context to get Local data
     */
    Main(Context context) {
        this.context = context;
    }

    void init() {
        AppInstance appInstance = AppInstance.getInstance();
        appInstance.getLokalData(context);
        appInstance.getClient().setup(context);
    }


}
