package net.icraftsystems.app.auth.android.comicfirebase.Interface;

import net.icraftsystems.app.auth.android.comicfirebase.Model.Comic;

import java.util.List;

public interface IComicLoadDone {
    void onComicLoadDoneListner(List<Comic> comicList);
}
