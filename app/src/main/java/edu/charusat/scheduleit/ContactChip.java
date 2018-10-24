package edu.charusat.scheduleit;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

/**
 * Created by HP on 23-10-2018.
 */

public class ContactChip implements ChipInterface {

    private String id;
    private Uri avatarUri;
    private String name;
    private String phoneNumber;

    public ContactChip(String id, Uri avatarUri, String name, String phoneNumber) {
        this.id = id;
        this.avatarUri = avatarUri;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }


    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return avatarUri;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return phoneNumber;
    }
}
