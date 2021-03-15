module com.bsplib {
    opens bsplib.log to java.logging;

    exports bsplib;
    exports bsplib.app;
    exports bsplib.entity;
    exports bsplib.nmo;
    exports bsplib.lump;
    exports bsplib.vector;
    exports bsplib.log;
    exports bsplib.struct;
    exports bsplib.util;
    exports bsplib.modules;
    exports bsplib.modules.entity;
    exports bsplib.modules.geom;
    exports bsplib.modules.texture;

    requires java.desktop;
    requires java.logging;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.tukaani.xz;
}