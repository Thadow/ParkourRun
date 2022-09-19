package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MessagesConfig extends ConfigurationManager {

    public MessagesConfig(String configName, String dir) {
        super(configName, dir);
        if (isFirstTime()) {
            YamlConfiguration configuration = getConfiguration();
            configuration.addDefault("Prefix", "&8[&6ParkourRun&8]");
            configuration.addDefault("No Permission", "%prefix% &cNo tienes el permiso necesario para ejecutar esta acción &7(&e%permNode%&7)");


            configuration.addDefault("Messages.Invalid Arena Parameter.Arena ID", "%prefix% &cParámetro Invalido (ArenaID / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Arena Name", "%prefix% &cParámetro Invalido (Arena Name / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Min Players", "%prefix% &cParámetro Invalido (Min Players / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Max Players", "%prefix% &cParámetro Invalido (Max Players / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Min > Max", "%prefix% &cParámetro Invalido (Min Players > Max Players / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Spawn Location", "%prefix% &cParámetro Invalido (Spawn Location / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Wait Location", "%prefix% &cParámetro Invalido (Wait Location / %arenaID%)");
            configuration.addDefault("Messages.Invalid Area Parameter.Wait To Start", "%prefix% &cParámetro Invalido (Wait To Start Time / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Wait To Re-Enable", "%prefix% &cParámetro Invalido (Wait To Re-Enable Time / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Max Time", "%prefix% &cParámetro Invalido (Max Time / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Win Zone Corner", "%prefix% &cParámetro Invalido (Win Zone Corner 1-2 / %arenaID%)");
            configuration.addDefault("Messages.Invalid Arena Parameter.Arena Zone Corner", "%prefix% &cParámetro Invalido (Arena Zone Corner 1-2 / %arenaID%)");


            configuration.addDefault("Messages.Arena.Arena Disabled", "%prefix% &cLa arena esta desactivada");
            configuration.addDefault("Messages.Arena.Arena Created", "%prefix% &aLa arena ha sido creada correctamente!");
            configuration.addDefault("Messages.Arena.Arena Deleted", "%prefix% &aLa arena ha sido eliminada correctamente!");
            configuration.addDefault("Messages.Arena.Arena Already Exists", "%prefix% &cLa arena ya existe!");
            configuration.addDefault("Messages.Arena.Can't Modify", "%prefix% &cNo puedes modificar un parámetro de una arena mientras esta activa!");
            configuration.addDefault("Messages.Arena.Can't Disable", "%prefix% &cNo puedes desactivar una arena mientras esta en juego!");
            configuration.addDefault("Messages.Arena.Arena Already Enabled", "%prefix% &cLa arena ya esta activada");
            configuration.addDefault("Messages.Arena.Arena Already Disabled", "%prefix% &cLa arena ya esta desactivada");


            configuration.addDefault("Messages.Parameter Changed.Arena Disabled.Message", "%prefix% &cLa arena ha sido desactivada correctamente!");
            configuration.addDefault("Messages.Parameter Changed.Arena Disabled.Message To Players", "%prefix% &cLa arena ha sido desactivada, todos han sido enviados al spawn.");
            configuration.addDefault("Messages.Parameter Changed.Arena Enabled", "%prefix% &aLa arena ha sido activada correctamente!");
            configuration.addDefault("Messages.Parameter Changed.Spawn Location Set", "%prefix% &aLocalización del spawn cambiada correctamente! &7(World: %world%, XYZ: %x%-%y%-%z%, Pitch: %pitch%, Yaw: %yaw% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Wait Location Set", "%prefix% &aLocalización del spawn de espera cambiada correctamente! &7(World: %world%, XYZ: %x%-%y%-%z%, Pitch: %pitch%, Yaw: %yaw% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Min Players Set", "%prefix% &aCantidad minima de jugadores cambiada correctamente! &7(Cantidad: %minPlayers% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Max Players Set", "%prefix% &aCantidad maxima de jugadores cambiada correctamente! &7(Cantidad: %maxPlayers% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Wait Time Set", "%prefix% &aTiempo de espera cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Wait Time To Re-Enable Set", "%prefix% &aTiempo de espera de re-activación cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Max Time Set", "%prefix% &aTiempo maximo cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Arena Name Set", "%prefix% &aNombre de la arena cambiado correctamente! &7(Nombre: %arenaName%&7 - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Win Zone Set", "%prefix% &aRegion de la WinZone cambiada correctamente! &7(XYZ: %x%-%y%-%z%, Esquina: %corner% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Arena Zone Set", "%prefix% &cRegion de la Arena cambiada correctamente! &7(XYZ: %x%-%y%-%z%, Esquina: %corner% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Checkpoint Added", "%prefix% &aCheckpoint añadido correctamente! &7(ID: %checkpointID%, Total: %totalCheckpoints% - ArenaID: %arenaID%)");
            configuration.addDefault("Messages.Parameter Changed.Checkpoint Deleted", "%prefix% &aCheckpoint removido correctamente! &7(ID: %checkpointID%, Total: %totalCheckpoints% - ArenaID: %arenaID%)");


            configuration.addDefault("Messages.Parameter Changed.Show Info.Enabled", true);
            List<String> infoMessage = new ArrayList<>();
            infoMessage.add("&m                                         ");
            infoMessage.add("");
            infoMessage.add("&eEnabled: %enabled%");
            infoMessage.add("&eArenaID: %arenaID%");
            infoMessage.add("&eArena Name: %arenaName%");
            infoMessage.add("&eWait To Start Time: %waitTime%");
            infoMessage.add("&eWait Time To Re-Enable: %reEnableTime%");
            infoMessage.add("&eMin Players: %minPlayers%");
            infoMessage.add("&eMax Players: %maxPlayers%");
            infoMessage.add("&eTotal Checkpoints: %totalCheckpoints");
            infoMessage.add("");
            infoMessage.add("&m                                         ");
            configuration.addDefault("Messages.Arena.Parameter Changed.Show Info.Message", infoMessage);


            configuration.addDefault("Messages.Arena.Already In Arena", "%prefix% &cYa estas en una arena!");
            configuration.addDefault("Messages.Arena.Nobody", "Nadie");
            configuration.addDefault("Messages.Arena.None", "Ninguno");
            configuration.addDefault("Messages.Arena.Unknown Arena", "%prefix% &cArena no valida");
            configuration.addDefault("Messages.Arena.In Game", "%prefix% &cLa arena ya ha comenzado!");
            configuration.addDefault("Messages.Arena.Ending", "%prefix% &cLa arena esta terminando!");
            configuration.addDefault("Messages.Arena.Full", "%prefix% &cLa arena esta llena!");
            configuration.addDefault("Messages.Arena.Countdown Stopped", "&cLa cuenta regresiva a sido detenida! No hay jugadores suficiente para iniciar la arena.");
            configuration.addDefault("Messages.Arena.Player Join", "&a%player% &ese ha unido (&b%current%&e/&b%max%&e)");
            configuration.addDefault("Messages.Arena.Player Leave In Waiting", "&a%player% &cse ha ido &e(&b%current%&e/&b%max%&e)");
            configuration.addDefault("Messages.Arena.Player Leave In Game", "&a%player% &esalió");

            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Enabled", true);
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Fade In", 20);
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Stay", 30);
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Fade Out", 10);
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Title", "&aHaz alcanzado un checkpoint!");
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.SubTitle", "&aEn: &e%time%");
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Message.To Player", "&aHaz alcanzado el checkpoint: %checkpoint%! Siguiente: &e%nextCheckpoint%");
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Broadcast.Enabled", true);
            configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Broadcast.Message", "&aEl jugador &e%player% &aha llegado al checkpoint &e%checkpoint% &aen &e%time%&a!");


            configuration.addDefault("Messages.Arena.Starting In.Message", "&eEl juego iniciara en &c%seconds% &esegundos!");
            configuration.addDefault("Messages.Arena.Starting In.Titles.Enabled", true);
            configuration.addDefault("Messages.Arena.Starting In.Titles.Fade In", 20);
            configuration.addDefault("Messages.Arena.Starting In.Titles.Stay", 30);
            configuration.addDefault("Messages.Arena.Starting In.Titles.Fade Out", 10);

            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 10.Title", "&c10");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 10.SubTitle", "");

            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 5.Title", "&c5");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 5.SubTitle", "");

            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 4.Title", "&c4");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 4.SubTitle", "");

            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 3.Title", "&c3");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 3.SubTitle", "");

            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 2.Title", "&c2");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 2.SubTitle", "");

            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Message", "&eEl juego iniciara en &c1 &esegundo");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 1.Title", "&c1");
            configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 1.SubTitle", "");


            configuration.addDefault("Messages.Arena.Starting In.Sound.Enabled", true);
            configuration.addDefault("Messages.Arena.Staring In.Sound.Sound", "NOTE_PLING;10;10");


            List<String> startedMessage = new ArrayList<>();
            startedMessage.add("asd");
            configuration.addDefault("Messages.Arena.Started.Message", startedMessage);

            configuration.addDefault("Messages.Arena.Started.Titles.Enabled", true);
            configuration.addDefault("Messages.Arena.Started.Titles.Title", "&6Iniciamos!");
            configuration.addDefault("Messages.Arena.Started.Titles.SubTitle", "&eBuena suerte");
            configuration.addDefault("Messages.Arena.Started.Fade In", 20);
            configuration.addDefault("Messages.Arena.Started.Stay", 30);
            configuration.addDefault("Messages.Arena.Started.Fade Out", 10);

            configuration.addDefault("Messages.Arena.Started.Sound.Enabled", true);
            configuration.addDefault("Messages.Arena.Started.Sound.Sound", "NOTE_PLING;10;10");


            List<String> tieMessageList = new ArrayList<>();
            tieMessageList.add("&cEmpate! &eNadie a ganado dentro del tiempo propuesto");
            configuration.addDefault("Messages.Arena.Tie.Message", tieMessageList);

            configuration.addDefault("Messages.Arena.Tie.Titles.Enabled", true);
            configuration.addDefault("Messages.Arena.Tie.Titles.Title", "&cEmpate!");
            configuration.addDefault("Messages.Arena.Tie.Titles.SubTitle", "&eNadie a ganado dentro del tiempo propuesto");
            configuration.addDefault("Messages.Arena.Tie.Titles.Fade In", 20);
            configuration.addDefault("Messages.Arena.Tie.Titles.Stay", 30);
            configuration.addDefault("Messages.Arena.Tie.Titles.Fade Out", 10);

            configuration.addDefault("Messages.Arena.Tie.Sound.Enabled", true);
            configuration.addDefault("Messages.Arena.Tie.Sound.Sound", "NOTE_PLING;10;10");



            configuration.addDefault("Messages.Arena.Ended.Fireworks.Enabled", true);

            List<String> endedMessage = new ArrayList<>();
            endedMessage.add("&e&m                               ");
            endedMessage.add("");
            endedMessage.add("     &aPartida Finalizada!         ");
            endedMessage.add("    &6Ganador&8: &e%winner%        ");
            endedMessage.add("");
            endedMessage.add("&e&m                               ");
            configuration.addDefault("Messages.Arena.Ended.Message", endedMessage);

            configuration.addDefault("Messages.Arena.Ended.Titles.Winner.Enabled", true);
            configuration.addDefault("Messages.Arena.Ended.Titles.Winner.Title", "&6Victoria!");
            configuration.addDefault("Messages.Arena.Ended.Titles.Winner.SubTitle", "&aHas sido el primero en ganar el parkour!");
            configuration.addDefault("Messages.Arena.Ended.Titles.Winner.Fade In", 20);
            configuration.addDefault("Messages.Arena.Ended.Titles.Winner.Stay", 30);
            configuration.addDefault("Messages.Arena.Ended.Titles.Winner.Fade Out", 10);

            configuration.addDefault("Messages.Arena.Ended.Titles.Losers.Enabled", true);
            configuration.addDefault("Messages.Arena.Ended.Titles.Losers.Title", "&cDerrota!");
            configuration.addDefault("Messages.Arena.Ended.Titles.Losers.SubTitle", "&e%winner% ha ganado la partida");
            configuration.addDefault("Messages.Arena.Ended.Titles.Losers.Fade In", 20);
            configuration.addDefault("Messages.Arena.Ended.Titles.Losers.Stay", 30);
            configuration.addDefault("Messages.Arena.Ended.Titles.Losers.Fade Out", 10);

            configuration.addDefault("Messages.Arena.Ended.Sound.Enabled", true);
            configuration.addDefault("Messages.Arena.Ended.Sound.Sound", "NOTE_PLING;10;10");


            configuration.addDefault("Messages.Commands.Invalid Number", "&cNumero Invalido!");

            List<String> usagesMessage = new ArrayList<>();
            usagesMessage.add("&m&e---------------------------------");
            usagesMessage.add("");
            usagesMessage.add("&aComandos disponibles:              ");
            usagesMessage.add("");
            usagesMessage.add("&e/pkr createArena (arenaID)         ");
            usagesMessage.add("&e/pkr deleteArena (arenaID)         ");
            usagesMessage.add("&e/pkr disableArena (arenaID)        ");
            usagesMessage.add("&e/pkr enableArena (arenaID)         ");
            usagesMessage.add("&e/pkr join (arenaID)                ");
            usagesMessage.add("&e/pkr join random                   ");
            usagesMessage.add("&e/leave                             ");
            usagesMessage.add("&e/pkr setSpawn (arenaID)            ");
            usagesMessage.add("&e/pkr setWaitSpawn (arenaID)        ");
            usagesMessage.add("&e/pkr setMinPlayers (minPlayers) (arenaID)");
            usagesMessage.add("&e/pkr setMaxPlayers (maxPlayers) (arenaID)");
            usagesMessage.add("&e/pkr setWaitTime (time in seconds) (arenaID)");
            usagesMessage.add("&e/pkr setWaitTimeToReEnable (time in seconds) (arenaID)");
            usagesMessage.add("&e/pkr setMaxTime (time in seconds) (arenaID)");
            usagesMessage.add("&e/pkr setArenaName (arenaID) (arenaName)");
            usagesMessage.add("&e/pkr setWinZone pos1/pos2 (arenaID) ");
            usagesMessage.add("&e/pkr setArenaZone pos1/pos2 (arenaID)");
            usagesMessage.add("&e/pkr addCheckpoint (arenaID)        ");
            usagesMessage.add("&e/pkr deleteCheckpoint (arenaID)     ");
            usagesMessage.add("");
            usagesMessage.add("&m&e---------------------------------");
            configuration.addDefault("Messages.Commands.Main Command.Usages", usagesMessage);


            configuration.addDefault("Messages.Commanads.Leave.Can't Leave", "%prefix% &cEste comando solo puede ser ejecutado en arena.");

            configuration.options().copyDefaults(true);
            save();
        }
    }
}
