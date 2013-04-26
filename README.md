#VirtualBox Manager

 *Remotely monitor/manage virtual machines by calling vboxwebsrv SOAP web service.  
 *Monitor CPU/Memory metrics in realtime.  
 *Inspect & modify virtual machines settings.  
 *Take & restore snapshots.

##SSL Support

	SSL can be enabled when launching `vboxwebsrv` on the server machine.  You will need:
  *server certificate & key inside a PEM file
  *CA certificate in a CRT file
  *Launch `vboxwebsrv --host 0.0.0.0 --port 18084 --ssl --serverKey server.pem --ca ca.crt

Submodules
----------

This is a multi-module project.  It includes:
* [ActionBarSherlock](http://www.actionbarsherlock.com) - 3rd party library.
* [Tree-View-List-Android](http://code.google.com/p/tree-view-list-android/) - 3rd party library.  Tree view component.

