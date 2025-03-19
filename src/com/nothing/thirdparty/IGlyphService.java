package com.nothing.thirdparty;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IGlyphService extends IInterface {
    String DESCRIPTOR = "com.nothing.thirdparty.IGlyphService";

    void setFrameColors(int[] var1) throws RemoteException;

    void openSession() throws RemoteException;

    void closeSession() throws RemoteException;

    boolean register(String var1) throws RemoteException;

    boolean registerSDK(String var1, String var2) throws RemoteException;

    public abstract static class Stub extends Binder implements IGlyphService {
        static final int TRANSACTION_setFrameColors = 1;
        static final int TRANSACTION_openSession = 2;
        static final int TRANSACTION_closeSession = 3;
        static final int TRANSACTION_register = 4;
        static final int TRANSACTION_registerSDK = 5;

        public Stub() {
            this.attachInterface(this, "com.nothing.thirdparty.IGlyphService");
        }

        public static IGlyphService asInterface(IBinder obj) {
            if (obj == null) return null;
            else {
                IInterface iin = obj.queryLocalInterface("com.nothing.thirdparty.IGlyphService");
                return (IGlyphService)(iin != null && iin instanceof IGlyphService ? (IGlyphService)iin : new Proxy(obj));
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = "com.nothing.thirdparty.IGlyphService";
            if(code >= 1 && code <= 16777215) data.enforceInterface(descriptor);

            String _arg0;
            boolean _result;
            
            switch (code) {
                case 1598968902:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int[] colors = data.createIntArray();
                            data.enforceNoDataAvail();
                            this.setFrameColors(colors);
                            reply.writeNoException();
                            break;
                        case 2:
                            this.openSession();
                            reply.writeNoException();
                            break;
                        case 3:
                            this.closeSession();
                            reply.writeNoException();
                            break;
                        case 4:
                            _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            _result = this.register(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 5:
                            _arg0 = data.readString();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            _result = this.registerSDK(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }

                    return true;
            }
        }

        private static class Proxy implements IGlyphService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "com.nothing.thirdparty.IGlyphService";
            }

            public void setFrameColors(int[] colors) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("com.nothing.thirdparty.IGlyphService");
                    _data.writeIntArray(colors);
                    this.mRemote.transact(TRANSACTION_setFrameColors, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void openSession() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("com.nothing.thirdparty.IGlyphService");
                    this.mRemote.transact(TRANSACTION_openSession, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public void closeSession() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                try {
                    _data.writeInterfaceToken("com.nothing.thirdparty.IGlyphService");
                    this.mRemote.transact(TRANSACTION_closeSession, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

            }

            public boolean register(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("com.nothing.thirdparty.IGlyphService");
                    _data.writeString(key);
                    this.mRemote.transact(TRANSACTION_register, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readBoolean();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }

            public boolean registerSDK(String key, String device) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();

                boolean _result;
                try {
                    _data.writeInterfaceToken("com.nothing.thirdparty.IGlyphService");
                    _data.writeString(key);
                    _data.writeString(device);
                    this.mRemote.transact(TRANSACTION_registerSDK, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readBoolean();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }

                return _result;
            }
        }
    }

    public static class Default implements IGlyphService {
        public Default() {
        }

        public void setFrameColors(int[] colors) throws RemoteException {
        }

        public void openSession() throws RemoteException {
        }

        public void closeSession() throws RemoteException {
        }

        public boolean register(String key) throws RemoteException {
            return false;
        }

        public boolean registerSDK(String key, String device) throws RemoteException {
            return false;
        }

        public IBinder asBinder() {
            return null;
        }
    }
}
