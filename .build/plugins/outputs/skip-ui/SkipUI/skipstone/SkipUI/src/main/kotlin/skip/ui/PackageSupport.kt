package skip.ui

import skip.lib.*


internal val <E0, E1> Tuple2<E0, E1>.fromIndex: E0
    get() = element0

internal val <E0, E1> Tuple2<E0, E1>.toIndex: E1
    get() = element1

internal val <E0, E1, E2> Tuple3<E0, E1, E2>.x: E0
    get() = element0

internal val <E0, E1, E2> Tuple3<E0, E1, E2>.alignment: E1
    get() = element1

internal val <E0, E1, E2> Tuple3<E0, E1, E2>.y: E1
    get() = element1

internal val <E0, E1, E2> Tuple3<E0, E1, E2>.spacing: E2
    get() = element2

internal val <E0, E1, E2> Tuple3<E0, E1, E2>.z: E2
    get() = element2
