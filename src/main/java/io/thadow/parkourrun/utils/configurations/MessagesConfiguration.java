package io.thadow.parkourrun.utils.configurations;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.managers.ConfigurationManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MessagesConfiguration extends ConfigurationManager {
    public static MessagesConfiguration messagesConfiguration;

    public MessagesConfiguration() {
        super("messages", Main.getInstance().getDataFolder().getPath());
    }

    public static void init() {
        messagesConfiguration = new MessagesConfiguration();
        YamlConfiguration configuration = messagesConfiguration.getConfiguration();
        configuration.addDefault("Prefix", "&8[&6ParkourRun&8]");
        configuration.addDefault("No Permission", "%prefix% &cNo tienes el permiso necesario para ejecutar esta acción &7(&e%permNode%&7)");
        configuration.addDefault("Unknown Lobby", "%prefix% &cPrimero necesitas cambiar la localización del lobby! (&7/pkr setLobby)");


        configuration.addDefault("Messages.Invalid Arena Parameter.Arena ID", "%prefix% &cParámetro Invalido (ArenaID / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Arena Name", "%prefix% &cParámetro Invalido (Arena Name / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Min Players", "%prefix% &cParámetro Invalido (Min Players / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Max Players", "%prefix% &cParámetro Invalido (Max Players / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Min > Max", "%prefix% &cParámetro Invalido (Min Players > Max Players / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Spawn Location", "%prefix% &cParámetro Invalido (Spawn Location / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Wait Location", "%prefix% &cParámetro Invalido (Wait Location / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Wait To Start", "%prefix% &cParámetro Invalido (Wait To Start Time / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Re-Enable Time", "%prefix% &cParámetro Invalido (Re-Enable Time / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Ending Time", "%prefix% &cParámetro Invalido (Ending Time / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Max Time", "%prefix% &cParámetro Invalido (Max Time / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Win Zone Corner", "%prefix% &cParámetro Invalido (Win Zone Corner 1-2 / %arenaID%)");
        configuration.addDefault("Messages.Invalid Arena Parameter.Arena Zone Corner", "%prefix% &cParámetro Invalido (Arena Zone Corner 1-2 / %arenaID%)");


        configuration.addDefault("Messages.Arena.Arena Disabled", "%prefix% &cLa arena esta desactivada");
        configuration.addDefault("Messages.Arena.Arena Created", "%prefix% &aLa arena ha sido creada correctamente!");
        configuration.addDefault("Messages.Arena.Can't Create On Lobby", "%prefix% &cNo puedes crear una arena si tu server es un lobby!");
        configuration.addDefault("Messages.Arena.Arena Deleted", "%prefix% &aLa arena ha sido eliminada correctamente!");
        configuration.addDefault("Messages.Arena.Arena Already Exists", "%prefix% &cLa arena ya existe!");
        configuration.addDefault("Messages.Arena.Can't Modify", "%prefix% &cNo puedes modificar un parámetro de una arena mientras esta activa!");
        configuration.addDefault("Messages.Arena.Can't Disable", "%prefix% &cNo puedes desactivar una arena mientras esta en juego!");
        configuration.addDefault("Messages.Arena.Arena Already Enabled", "%prefix% &cLa arena ya esta activada");
        configuration.addDefault("Messages.Arena.Arena Already Disabled", "%prefix% &cLa arena ya esta desactivada");


        configuration.addDefault("Messages.Arena.Parameter Changed.Arena Disabled.Message", "%prefix% &aLa arena ha sido desactivada correctamente!");
        configuration.addDefault("Messages.Arena.Parameter Changed.Arena Disabled.Message To Players", "%prefix% &cLa arena ha sido desactivada, todos han sido enviados al spawn.");
        configuration.addDefault("Messages.Arena.Parameter Changed.Arena Enabled", "%prefix% &aLa arena ha sido activada correctamente!");
        configuration.addDefault("Messages.Arena.Parameter Changed.Spawn Location Set", "%prefix% &aLocalización del spawn cambiada correctamente! &7(World: %world%, XYZ: %x%/%y%/%z%, Pitch: %pitch%, Yaw: %yaw% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Wait Location Set", "%prefix% &aLocalización del spawn de espera cambiada correctamente! &7(World: %world%, XYZ: %x%/%y%/%z%, Pitch: %pitch%, Yaw: %yaw% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Min Players Set", "%prefix% &aCantidad minima de jugadores cambiada correctamente! &7(Cantidad: %minPlayers% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Max Players Set", "%prefix% &aCantidad maxima de jugadores cambiada correctamente! &7(Cantidad: %maxPlayers% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Wait Time Set", "%prefix% &aTiempo de espera cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Re-Enable Time Set", "%prefix% &aTiempo de re-activación cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Ending Time Set", "%prefix% &aTiempo de finalización cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Max Time Set", "%prefix% &aTiempo maximo cambiado correctamente! &7(Tiempo: %time% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Arena Name Set", "%prefix% &aNombre de la arena cambiado correctamente! &7(Nombre: %arenaName%&7 - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Win Zone Set", "%prefix% &aRegion de la WinZone cambiada correctamente! &7(XYZ: %x%/%y%/%z%, Esquina: %corner% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Arena Zone Set", "%prefix% &aRegion de la ArenaZone cambiada correctamente! &7(XYZ: %x%/%y%/%z%, Esquina: %corner% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Waiting Zone Set", "%prefix% &aRegion de la WaitingZone cambiada correctamente! &7(XYZ: %x%/%y%/%z%, Esquina: %corner% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Checkpoint Added", "%prefix% &aCheckpoint añadido correctamente! &7(ID: %checkpointID%, Total: %totalCheckpoints% - ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Parameter Changed.Checkpoint Deleted", "%prefix% &aCheckpoint removido correctamente! &7(ID: %checkpointID%, Total: %totalCheckpoints% - ArenaID: %arenaID%)");


        configuration.addDefault("Messages.Arena.Parameter Changed.Show Info.Enabled", true);
        List<String> infoMessage = new ArrayList<>();
        infoMessage.add("&m                                         ");
        infoMessage.add("");
        infoMessage.add("&eEnabled: %enabled%");
        infoMessage.add("&eArenaID: %arenaID%");
        infoMessage.add("&eArena Name: %arenaName%");
        infoMessage.add("&eWait To Start Time: %waitTime%");
        infoMessage.add("&eRe-Enable Time: %reEnableTime%");
        infoMessage.add("&eEnding Time: %endingTime%");
        infoMessage.add("&eMax Time: %maxTime%");
        infoMessage.add("&eMin Players: %minPlayers%");
        infoMessage.add("&eMax Players: %maxPlayers%");
        infoMessage.add("&eTotal Checkpoints: %totalCheckpoints%");
        infoMessage.add("");
        infoMessage.add("&m                                         ");
        configuration.addDefault("Messages.Arena.Parameter Changed.Show Info.Message", infoMessage);


        configuration.addDefault("Messages.Signs.Sign Added", "%prefix% &aCartel agregado correctamente! &7(ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Signs.Sign Removed", "%prefix% &cCartel removido correctamente! &7(ArenaID: %arenaID%)");
        configuration.addDefault("Messages.Arena.Already In Arena", "%prefix% &cYa estas en una arena!");
        configuration.addDefault("Messages.Arena.Nobody", "Nadie");
        configuration.addDefault("Messages.Arena.None", "Ninguno");
        configuration.addDefault("Messages.Arena.No Arenas Available", "%prefix% &cNo hay ninguna arena disponible en este momento.");
        configuration.addDefault("Messages.Arena.Unknown Arena", "%prefix% &cArena no valida");
        configuration.addDefault("Messages.Arena.Playing", "%prefix% &cLa arena ya ha comenzado!");
        configuration.addDefault("Messages.Arena.Ending", "%prefix% &cLa arena esta terminando!");
        configuration.addDefault("Messages.Arena.Restarting", "%prefix% &cLa arena se esta reiniciando!");
        configuration.addDefault("Messages.Arena.Full", "%prefix% &cLa arena esta llena!");
        configuration.addDefault("Messages.Arena.Countdown Stopped", "&cLa cuenta regresiva a sido detenida! No hay jugadores suficiente para iniciar la arena.");
        configuration.addDefault("Messages.Arena.Player Join", "&a%player% &ese ha unido (&b%current%&e/&b%max%&e)");
        configuration.addDefault("Messages.Arena.Player Leave In Waiting", "&a%player% &cse ha ido &e(&b%current%&e/&b%max%&e)");
        configuration.addDefault("Messages.Arena.Player Leave In Game", "&a%player% &esalió");

        configuration.addDefault("Messages.Arena.Checkpoint.Need All", "&cNecesitas todos los checkpoints para ganar!");
        configuration.addDefault("Messages.Arena.Checkpoint.No Checkpoints", "&cLa arena no tiene ningun checkpoint!");
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Enabled", true);
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Fade In", 20);
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Stay", 30);
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Fade Out", 10);
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.Title", "&aHaz alcanzado un checkpoint!");
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Titles.SubTitle", "&aEn: &e%time%");
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Message.To Player", "&aHaz alcanzado el checkpoint: %checkpoint%! Siguiente: &e%nextCheckpoint%");
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Message.Broadcast.Enabled", true);
        configuration.addDefault("Messages.Arena.Checkpoint.On Get Checkpoint.Message.Broadcast.Message", "&aEl jugador &e%player% &aha llegado al checkpoint &e%checkpoint% &aen &e%time%&a!");


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

        configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 1.Message", "&eEl juego iniciara en &c1 &esegundo");
        configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 1.Title", "&c1");
        configuration.addDefault("Messages.Arena.Starting In.Broadcast.Second 1.SubTitle", "");


        configuration.addDefault("Messages.Arena.Starting In.Sound.Enabled", true);
        configuration.addDefault("Messages.Arena.Starting In.Sound.Sound", "NOTE_PLING;10;10");


        List<String> startedMessage = new ArrayList<>();
        startedMessage.add("&6Iniciamos!");
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
        usagesMessage.add("&m&8---------&6ParkourRun&m&8---------");
        usagesMessage.add("");
        usagesMessage.add("&aComandos disponibles:              ");
        usagesMessage.add("");
        usagesMessage.add("&6/pkr createArena (arenaID) &8- &7Te permite crear una arena.");
        usagesMessage.add("&6/pkr deleteArena (arenaID) &8- &7Te permite eliminar una arena.");
        usagesMessage.add("&6/pkr disableArena (arenaID) &8- &7Te permite desactivar una arena.");
        usagesMessage.add("&6/pkr enableArena (arenaID) &8- &7Te permite activar una arena.");
        usagesMessage.add("&6/pkr join (arenaID) &8- &7Entras a una arena especificada.");
        usagesMessage.add("&6/pkr join random &8- &7Entras a una arena random.");
        usagesMessage.add("&6/leave &8- &7Te permite salir de una arena.");
        usagesMessage.add("&6/pkr build &8- &7Te permitira contruir en el lobby o en las arenas.");
        usagesMessage.add("&6/prk setLobby &8- &7Establace la localizacion del lobby.");
        usagesMessage.add("&6/pkr setSpawn (arenaID) &8- &7Establece la localizacion de inicio de la arena.");
        usagesMessage.add("&6/pkr setWaitSpawn (arenaID) &8- &7Establece la localicazion de espera de la arena.");
        usagesMessage.add("&6/pkr setMinPlayers (minPlayers) (arenaID) &8- &7Cambia la cantidad minima de jugadores para que inicie la arena.");
        usagesMessage.add("&6/pkr setMaxPlayers (maxPlayers) (arenaID) &8- &7Cambia la cantidad maxima de jugadores que pueden entrar a la arena.");
        usagesMessage.add("&6/pkr setWaitTime (time in seconds) (arenaID) &8- &7Cambia la cantidad de segundos que se tiene que esperar para que la arena inicie.");
        usagesMessage.add("&6/pkr setReEnableTime (time in seconds) (arenaID) &8- &7Cambia la cantidad de segundos en que la arena se va a volver a activar despues de finalizada por completo.");
        usagesMessage.add("&6/pkr setEndingTime (time in seconds) (arenaID) &8- &7Cambia la cantidad de segundos en que la arena finalizara por completo.");
        usagesMessage.add("&6/pkr setMaxTime (time in seconds) (arenaID) &8- &7Cambia el tiempo maximo de la arena.");
        usagesMessage.add("&6/pkr setArenaName (arenaID) (arenaName) &8- &7Cambia el nombre de la arena.");
        usagesMessage.add("&6/pkr setWinCorner 1/2 &8- &7Cambia el limite de la la WinZone de la arena.");
        usagesMessage.add("&6/pkr setArenaCorner 1/2 &8- &7Cambia el limite de la ArenaZone de la arena.");
        usagesMessage.add("&6/pkr setWaitingCorner 1/2 &8- &7Cambia el limite de la WaitingZone de la arena.");
        usagesMessage.add("&6/pkr addCheckpoint (arenaID) &8- &7Añade un checkpoint a la arena.");
        usagesMessage.add("&6/pkr deleteLastCheckpoint (arenaID) &8- &7Elimina el ultimo checkpoint de la arena.");
        usagesMessage.add("");
        usagesMessage.add("&m&8---------&6ParkourRun&m&8---------");
        configuration.addDefault("Messages.Commands.Main Command.Usages", usagesMessage);
        configuration.addDefault("Messages.Commands.Main Command.Selecting Corner", "%prefix% &aPor favor seleccione la esquina rompiendo un bloque.");
        configuration.addDefault("Messages.Commands.Main Command.Lobby Location Set", "%prefix% &aLocalización del lobby cambiada correctamente! &7(World: %world%, XYZ: %x%/%y%/%z%, Pitch: %pitch%, Yaw: %yaw%)");
        configuration.addDefault("Messages.Commands.Main Command.Build Enabled", "%prefix% &aAhora puedes construir!");
        configuration.addDefault("Messages.Commands.Main Command.Build Disabled", "%prefix% &cYa no puedes contruir");


        configuration.addDefault("Messages.Commands.Leave.Can't Leave", "%prefix% &cEste comando solo puede ser ejecutado en arena.");

        configuration.options().copyDefaults(true);
        messagesConfiguration.save();
    }
}
