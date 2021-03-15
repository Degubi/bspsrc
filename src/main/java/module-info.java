module com.bspsrc {
    opens log to java.logging;

    requires commons.cli;
    requires java.desktop;
    requires java.logging;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.tukaani.xz;
}