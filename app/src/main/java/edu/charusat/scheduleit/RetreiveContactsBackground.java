package edu.charusat.scheduleit;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 23-10-2018.
 */

public class RetreiveContactsBackground extends AsyncTask<Object,Object,List<ContactChip>> {

    public MainActivity mActivity;
    private List<ContactChip> mContactList;
    public RetreiveContactsBackground(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected void onPreExecute() {
        mContactList = new ArrayList<>();
    }

    @Override
    protected List<ContactChip> doInBackground(Object[] params) {
        Cursor phones = mActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);

        // loop over all contacts
        if(phones != null) {
            while (phones.moveToNext()) {
                // get contact info
                String phoneNumber = null;
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String avatarUriString = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Uri avatarUri = null;
                if(avatarUriString != null)
                    avatarUri = Uri.parse(avatarUriString);

                // get phone number
                if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

                    while (pCur != null && pCur.moveToNext()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    pCur.close();

                }
                ContactChip contactChip = new ContactChip(id, avatarUri, name, phoneNumber);
                mContactList.add(contactChip);
            }
            phones.close();
        }
        return mContactList;
    }

    @Override
    protected void onPostExecute(List<ContactChip> mContactList) {
        MainActivity.mContactList = mContactList;
    }
}
