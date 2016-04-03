package com.cs246.senselab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.Toast;

import com.cs246.senselab.model.LabReport;
import com.cs246.senselab.storage.Children;
import com.cs246.senselab.storage.ChildrenAdapter;
import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.googledrive.DriveChildrenAdapter;
import com.cs246.senselab.storage.Folder;
import com.google.api.services.drive.model.Permission;


public class DisplayFolderActivity extends BaseActivity {
    private Context context = this;
    private ChildrenAdapter mChildrenAdapter = null;
    private static final String TAG = DisplayFolderActivity.class.getName();
    private ListView list;
    private final Activity ACTIVITY = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_folder);
        provider.connect(new StorageProvider.ConnectCallback()
        {
            @Override
            public void onConnect(StorageProvider provider) {
                setParentFolder();
                initializeAddDataTypeButton();
                listFolderContents();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (parentDisplayData != null) {
            Intent intent = new Intent(this, DisplayFolderActivity.class);
            intent.putExtra(EXTRA_FOLDERNAME, parentName);
            intent.putExtra(EXTRA_FOLDERID, parentId);
            intent.putExtra(EXTRA_DISPLAYDATA, parentDisplayData);
            startActivity(intent);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mChildrenAdapter != null)
            mChildrenAdapter.clear();
    }

    private void initializeAddDataTypeButton() {
        Button addDisplayData = (Button) findViewById(R.id.add_new_button);
        addDisplayData.setText("ADD NEW " + displayData);
        addDisplayData.setVisibility(View.VISIBLE);
        addDisplayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreationWizardClickListener(v);
            }
        });
    }

    private void listFolderContents() {
        provider.getFolder().listChildrenAsync(new Folder.ListChildrenCallback() {
            @Override
            public void onChildrenListed(Children children) {
                mChildrenAdapter = new DriveChildrenAdapter(context);
                list = (ListView) findViewById(R.id.senselab_list);
                mChildrenAdapter.append(children);
                list.setAdapter(mChildrenAdapter.getDataAdapter());
                setListClickListener(children);
                alertEmail(children);
            }
        });
    }

    private void setListClickListener(final Children children) {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String newFolderId = mChildrenAdapter.getIdList().get(position);
                String newFolderName = mChildrenAdapter.getNameList().get(position);
                String newDisplayData = getDisplayData();
                Class whereToGo = DisplayFolderActivity.class;

                if (newDisplayData.equals("Section Data")) {
                    whereToGo = DisplaySectionActivity.class;
                }

                Intent intent = new Intent(getBaseContext(), whereToGo);
                intent.putExtra(EXTRA_FOLDERID, newFolderId);
                intent.putExtra(EXTRA_FOLDERNAME, newFolderName);
                intent.putExtra(EXTRA_DISPLAYDATA, newDisplayData);

                startActivity(intent);
            }
        });
    }

    private String getDisplayData() {
        String newDisplayData = null;

        switch (displayData) {
            case "Lab Report":
                newDisplayData = "Section";
                break;
            case "Section" :
                newDisplayData = "Section Data";
                break;
            default:
                newDisplayData = "Lab Report";
        }

        Log.d(TAG, "The new data type is " + newDisplayData);
        return newDisplayData;
    }

    public void toCreationWizardClickListener(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create " + displayData);

        final EditText input = new EditText(this);
        input.setHint("Name of " + displayData);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = input.getText().toString();

                provider.getFolder().createSubFolderAsync(name, new Folder.CreateFolderCallback() {
                    @Override
                    public void onCreate() {
                        listFolderContents();
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void alertEmail(final Children children) {
       if (displayData.equals("Lab Report")) {
           list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               @Override
               public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                   AlertDialog.Builder alert = new AlertDialog.Builder(ACTIVITY);
                   alert.setTitle("Share Lab Report through email");

                   final EditText input = new EditText(ACTIVITY);
                   input.setHint("Email address");
                   alert.setView(input);

                   alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                           final String email = input.getText().toString();

                           String title = children.getNames().get(position).toString();
                           String reportId = children.getIds().get(position).toString();

                           LabReport labReport = new LabReport(title, ACTIVITY, provider);

                           final ProgressDialog prog = new ProgressDialog(ACTIVITY);
                           prog.setIndeterminate(true);
                           prog.setCancelable(false);
                           prog.setMessage("Compiling Lab Report...");
                           prog.show();

                           labReport.get(reportId, new LabReport.LoadCallback() {
                               @Override
                               public void onLoad(String labReport) {
                                   System.out.println(labReport);
                                   sendEmail(labReport, email);
                                   prog.dismiss();
                               }
                           });
                       }
                   });

                   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                           // Canceled.
                       }
                   });

                   alert.show();
                   return false;
               }
           });
       }
    }

    private void sendEmail(String content, String email) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "Lab Report");
        i.putExtra(Intent.EXTRA_TEXT   , content);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
