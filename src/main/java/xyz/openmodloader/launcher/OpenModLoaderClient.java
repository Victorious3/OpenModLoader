package xyz.openmodloader.launcher;

import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.event.strippable.Side;

import java.io.File;
import java.net.Proxy;
import java.util.Arrays;

public class OpenModLoaderClient {
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../.gradle/minecraft/natives/").getAbsolutePath());
        OMLSideTransformer.SIDE = Side.CLIENT;
        LaunchArguments arguments = new LaunchArguments(args);

        if (arguments.containsArgument("password")) {
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1")).createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(arguments.getArgument("username"));
            auth.setPassword(arguments.getArgument("password"));
            arguments.removeArgument("password");

            try {
                auth.logIn();
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return;
            }

            arguments.addArgument("accessToken", auth.getAuthenticatedToken());
            arguments.addArgument("uuid", auth.getSelectedProfile().getId().toString().replace("-", ""));
            arguments.addArgument("username", auth.getSelectedProfile().getName());
            arguments.addArgument("userType", auth.getUserType().getName());
            arguments.addArgument("userProperties", new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create().toJson(auth.getUserProperties()));
        }

        arguments.addArgument("version", "1.10");
        arguments.addArgument("assetIndex", "1.10");
        arguments.addArgument("accessToken", "OpenModLoader");
        arguments.addArgument("tweakClass", "xyz.openmodloader.launcher.OMLTweaker");

        System.out.println(Arrays.toString(arguments.getArguments()));
        Launch.main(arguments.getArguments());
    }
}
