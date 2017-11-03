package com.codextech.ibtisam.lepak_app.fragments;


import android.support.v4.app.Fragment;


public class NfcFragment extends Fragment {
//    public static final String TAG = NfcFragment.class.getSimpleName();
//    private TextView text;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_main, container, false);
//        text = (TextView) view.findViewById(R.id.text);
//        return view;
//    }
//
//    // list of NFC technologies detected:
//    private final String[][] techList = new String[][]{
//            new String[]{
//                    NfcA.class.getName(),
//                    NfcB.class.getName(),
//                    NfcF.class.getName(),
//                    NfcV.class.getName(),
//                    IsoDep.class.getName(),
//                    MifareClassic.class.getName(),
//                    MifareUltralight.class.getName(), Ndef.class.getName()
//            }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // creating pending intent:
//        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        // creating intent receiver for NFC events:
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
//        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
//        // enabling foreground dispatch for getting intent from NFC event:
//        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
//        nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, new IntentFilter[]{filter}, this.techList);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        // disabling foreground dispatch:
//        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
//        nfcAdapter.disableForegroundDispatch(getActivity());
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
//            text.setText("NFC Tag\n" + ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
//        }
//    }
//
//    private String ByteArrayToHexString(byte[] inarray) {
//        int i, j, in;
//        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
//        String out = "";
//
//        for (j = 0; j < inarray.length; ++j) {
//            in = (int) inarray[j] & 0xff;
//            i = (in >> 4) & 0x0f;
//            out += hex[i];
//            i = in & 0x0f;
//            out += hex[i];
//        }
//        return out;
//    }
}