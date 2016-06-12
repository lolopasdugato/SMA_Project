package ca.uqac.viallet.benet.sma_carpool.container;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.logging.Level;

import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolFindAgent;
import ca.uqac.viallet.benet.sma_carpool.agent.CarpoolOfferAgent;
import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

/**
 * Created by Etienne on 2016-06-12.
 */
public class Container {

    // Jade attributes
    private Logger logger = Logger.getJADELogger(this.getClass().getName());
    private MicroRuntimeServiceBinder microRuntimeServiceBinder;
    private ServiceConnection serviceConnection;

    private String host = "10.0.2.2";
    private String port = "1099";

    private static Container instance;

    private Container() {}

    public static Container getInstance() {
        if (instance == null) {
            instance = new Container();
        }
        return instance;
    }


    public void startCarpool(final String nickname,
                             final String agentClass,
                             final Object[] args,
                             Activity app) {
        logger.log(Level.INFO, "Starting environment");
        final Properties profile = new Properties();
        profile.setProperty(Profile.MAIN_HOST, host);
        profile.setProperty(Profile.MAIN_PORT, port);
        profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
        profile.setProperty(Profile.JVM, Profile.ANDROID);

        //    if (AndroidHelper.isEmulator()) {
        // Emulator: this is needed to work with emulated devices
        profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
        logger.log(Level.INFO,AndroidHelper.LOOPBACK);
      /*  } else {
            profile.setProperty(Profile.LOCAL_HOST,
                    AndroidHelper.getLocalIPAddress());
        }*/
        // Emulator: this is not really needed on a real device
        profile.setProperty(Profile.LOCAL_PORT, "2000");

        if (microRuntimeServiceBinder == null) {
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
                    logger.log(Level.INFO, "Gateway successfully bound to MicroRuntimeService");
                    startContainer(nickname, agentClass, args, profile);
                };

                public void onServiceDisconnected(ComponentName className) {
                    microRuntimeServiceBinder = null;
                    logger.log(Level.INFO, "Gateway unbound from MicroRuntimeService");
                }
            };
            logger.log(Level.INFO, "Binding Gateway to MicroRuntimeService...");
            app.bindService(new Intent(app.getApplicationContext(),
                            MicroRuntimeService.class), serviceConnection,
                    Context.BIND_AUTO_CREATE);
        } else {
            logger.log(Level.INFO, "MicroRumtimeGateway already binded to service");
            startAgent(nickname, agentClass, args);
        }
    }

    public void stopCarpool () {
        microRuntimeServiceBinder
                .stopAgentContainer(new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.log(Level.SEVERE, "Failed to stop the agents");
                        //agentStartupCallback.onFailure(throwable);
                    }
                });
    }


    public void startContainer(final String nickname, final String agentClass, final Object[] args, Properties profile) {
        if (!MicroRuntime.isRunning()) {
            microRuntimeServiceBinder.startAgentContainer(profile,
                    new RuntimeCallback<Void>() {
                        @Override
                        public void onSuccess(Void thisIsNull) {
                            logger.log(Level.INFO, "Successfully start of the container...");
                            startAgent(nickname, agentClass, args);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            logger.log(Level.SEVERE, "Failed to start the container...");
                        }
                    });
        } else {
        }
    }

    public void startAgent(final String nickname, final String agentClass, final Object[] args) {
        microRuntimeServiceBinder.startAgent(nickname,
                agentClass,
                args,
                new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                        logger.log(Level.INFO, "Successfully start of the "
                                + CarpoolFindAgent.class.getName() + "...");
                        try {
                            agentStartupCallback.onSuccess(MicroRuntime
                                    .getAgent(nickname));
                        } catch (ControllerException e) {
                            // Should never happen
                            agentStartupCallback.onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        logger.log(Level.SEVERE, "Failed to start the "
                                + CarpoolFindAgent.class.getName() + "...");
                        agentStartupCallback.onFailure(throwable);
                    }
                });
    }


    private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
        @Override
        public void onSuccess(AgentController agent) {
        }

        @Override
        public void onFailure(Throwable throwable) {
            logger.log(Level.INFO, "Nickname already in use!");
        }
    };

}
