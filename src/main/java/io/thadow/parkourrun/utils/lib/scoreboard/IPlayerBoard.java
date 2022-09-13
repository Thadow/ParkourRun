package io.thadow.parkourrun.utils.lib.scoreboard;

import java.util.Map;

public interface IPlayerBoard<V, N, S> {
    V get(final N p0);

    void set(final V p0, final N p1);

    void setAll(final V... p0);

    void clear();

    void remove(final N p0);

    void delete();

    S getName();

    void setName(final S p0);

    Map<N, V> getLines();
}
