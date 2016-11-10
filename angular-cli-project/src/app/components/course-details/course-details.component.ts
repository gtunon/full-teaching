import { Component, OnInit, OnChanges, Input, EventEmitter, trigger, state, animate, transition, style } from '@angular/core';
import { ActivatedRoute, Params }   from '@angular/router';
import { Subscription }             from 'rxjs/Subscription';

import { CommentComponent } from '../comment/comment.component';

import { CourseDetailsModalDataService } from '../../services/course-details-modal-data.service';
import { CourseService }         from '../../services/course.service';
import { SessionService }        from '../../services/session.service';
import { ForumService }          from '../../services/forum.service';
import { AuthenticationService } from '../../services/authentication.service';

import { Session }       from '../../classes/session';
import { Course }        from '../../classes/course';
import { Entry }         from '../../classes/entry';
import { Comment }       from '../../classes/comment';

@Component({
  selector: 'app-course-details',
  templateUrl: './course-details.component.html',
  styleUrls: ['./course-details.component.css'],
  animations: [
    trigger('fadeAnim', [
      state('commentsShown', style({
        opacity: 1
      })),
      state('commentsHidden', style({
        opacity: 0.2
      })),
      transition('commentsHidden => commentsShown', animate('.4s')),
      transition('commentsShown => commentsHidden', animate('.1s')),
    ]),
  ]
})
export class CourseDetailsComponent implements OnInit {

  course: Course;

  selectedEntry: Entry;

  fadeAnim = 'commentsHidden';

  //POST MODAL
  inputTitle: string;
  inputComment: string;
  inputDate: Date;
  inputTime: string;
  postModalMode: number = 3; //postModalMode: 0 -> New entry | 1 -> New comment | 2 -> New replay | 3 -> New session | 4 -> Add attenders
  postModalTitle: string = "New session";
  postModalEntry: Entry;
  postModalCommentReplay: Comment;

  //PUT-DELETE MODAL
  inputSessionTitle: string;
  inputSessionDescription: string;
  inputSessionDate: Date;
  inputSessionTime: string;
  updatedSession: Session;
  updatedSessionDate: string;
  allowSessionDeletion: boolean = false;
  allowForumEdition: boolean = false;
  checkboxForumEdition: string;
  putdeleteModalMode: number = 0; //putdeleteModalMode: 0 -> Modify session | 1 -> Modify forum | 2 -> Modify files
  putdeleteModalTitle: string = "Modify session";

  private actions2 = new EventEmitter<string>();
  private actions3 = new EventEmitter<string>();

  subscription1: Subscription;
  subscription2: Subscription;

  constructor(
    private courseService: CourseService,
    private forumService: ForumService,
    private sessionService: SessionService,
    private authenticationService: AuthenticationService,
    private route: ActivatedRoute,
    private courseDetailsModalDataService: CourseDetailsModalDataService) {
    this.subscription1 = courseDetailsModalDataService.postModeAnnounced$.subscribe(
      objs => {
        //objs is an array containing postModalMode, postModalEntry and postModalCommentReplay, in that specific order
        this.postModalMode = objs[0];
        this.postModalTitle = objs[1];
        if (objs[2]) this.postModalEntry = objs[2]; //Only if the string is not empty
        if (objs[3]) this.postModalCommentReplay = objs[3]; //Only if the string is not empty
      });
    this.subscription2 = courseDetailsModalDataService.putdeleteModeAnnounced$.subscribe(
      objs => {
        //objs is an array containing putdeleteModalMode and putdeleteModalTitle, in that specific order
        this.putdeleteModalMode = objs[0];
        if (objs[1]) this.putdeleteModalTitle = objs[1]; //Only if the string is not empty
      });
  }

  ngOnInit() {
    this.route.params.forEach((params: Params) => {
      let id = +params['id'];
      this.courseService.getCourse(id).subscribe(
        course => {
          console.log("Course " + course.id + ":");
          console.log(course);
          this.sortSessionsByDate(course.sessions);
          this.course = course;
          this.selectedEntry = this.course.courseDetails.forum.entries[0]; //selectedEntry default to first entry
          if (this.course.sessions.length > 0) this.changeUpdatedSession(this.course.sessions[0]); //updatedSession default to first session
          this.updateCheckboxForumEdition(this.course.courseDetails.forum.activated);
        },
        error => console.log(error));
    });
  }

  updatePostModalMode(mode: number, title: string, header: Entry, commentReplay: Comment) {
    let objs = [mode, title, header, commentReplay];
    this.courseDetailsModalDataService.announcePostMode(objs);
  }

  updatePutDeleteModalMode(mode: number, title: string){
    let objs = [mode, title];
    this.courseDetailsModalDataService.announcePutdeleteMode(objs);
  }

  getLastEntryComment(entry: Entry){
    let comment = entry.comments[0];
    for (let c of entry.comments){
      if (c.date > comment.date) comment = c;
      comment = this.recursiveReplyDateCheck(comment);
    }
    return comment;
  }

  numberToDate(d: number){
    return new Date(d);
  }

  changeUpdatedSession(session: Session){
    this.updatedSession = session;
    this.updatedSessionDate = (new Date(this.updatedSession.date)).toISOString().split("T")[0]; //YYYY-MM-DD format
    this.inputSessionTitle = this.updatedSession.title;
    this.inputSessionDescription = this.updatedSession.description;
    this.inputSessionDate = new Date(this.updatedSession.date);
    this.inputSessionTime = this.dateToTimeInputFormat(this.inputSessionDate);
  }

  isCurrentPostMode(possiblePostModes: string[]): boolean {
    return (possiblePostModes.indexOf(this.postModalMode.toString()) > -1);
  }

  isCurrentPutdeleteMode(possiblePutdeleteModes: string[]): boolean {
    return (possiblePutdeleteModes.indexOf(this.putdeleteModalMode.toString()) > -1);
  }

  updateCheckboxForumEdition(b: boolean){
    if (b){
      this.checkboxForumEdition = "DEACTIVATION";
    } else {
      this.checkboxForumEdition = "ACTIVATION";
    }
  }

  //POST new Entry, Comment or Session
  onCourseDetailsSubmit() {

    //If modal is opened in "New Entry" mode
    if (this.postModalMode === 0) {
      console.log("Saving new Entry: Title -> " + this.inputTitle + "  |  Comment -> " + this.inputComment);
      let e = new Entry(this.inputTitle, [new Comment(this.inputComment, null)]);

      this.forumService.newEntry(e, this.course.courseDetails.id).subscribe( //POST method requires an Entry and the CourseDetails id which contains its Forum
        response  => {
          console.log(response);
          this.course.courseDetails.forum = response; //Only on succesful post we update the modified forum
          this.actions2.emit("closeModal");
        },
        error => console.log(error)
      );
    }

    //If modal is opened in "New Session" mode
    else if (this.postModalMode === 3) {
      let date = new Date(this.inputDate);
      let hoursMins = this.inputTime.split(":");
      date.setHours(parseInt(hoursMins[0]), parseInt(hoursMins[1]));
      let s = new Session(this.inputTitle, this.inputComment, date.getTime());
      console.log(s);
      this.sessionService.newSession(s, this.course.id).subscribe(
        response => {
          console.log(response);
          this.sortSessionsByDate(response.sessions);
          this.course = response;
          this.actions2.emit("closeModal");
        },
        error => console.log(error)
      );
    }

    //If modal is opened in "New Comment" mode (replaying or not replaying)
    else {
      let c = new Comment(this.inputComment, this.postModalCommentReplay);
      console.log(c);
      this.forumService.newComment(c, this.selectedEntry.id, this.course.courseDetails.id).subscribe(
        response => {
          console.log(response);
          //Only on succesful post we locally update the created entry
          let ents = this.course.courseDetails.forum.entries;
          for (let i = 0; i < ents.length; i++) {
            if (ents[i].id == this.selectedEntry.id) {
              this.course.courseDetails.forum.entries[i] = response; //The entry with the required ID is updated
              this.selectedEntry = this.course.courseDetails.forum.entries[i];
              break;
            }
          }
          this.actions2.emit("closeModal");
        },
        error => console.log(error)
      );
    }
  }

  //PUT existing Session or Forum
  onPutDeleteSubmit(){

    //If modal is opened in PUT existing Session
    if(this.putdeleteModalMode === 0){
      let modifiedDate: number = this.fromInputToNumberDate(this.updatedSessionDate, this.inputSessionTime);
      let s: Session = new Session(this.inputSessionTitle, this.inputSessionDescription, modifiedDate);
      s.id = this.updatedSession.id; //The new session must have the same id as the modified session in order to replace it
      this.sessionService.editSession(s).subscribe(
        response => {
          console.log(response);
          //Only on succesful put we locally update the modified session
          for (let i = 0; i < this.course.sessions.length; i++) {
            if (this.course.sessions[i].id == response.id) {
              this.course.sessions[i] = response; //The session with the required ID is updated
              this.updatedSession = this.course.sessions[i];
              break;
            }
          }
          this.actions3.emit("closeModal");
        },
        error => console.log(error)
      );
    }

    //If modal is opened in PUT existing Forum
    else if (this.putdeleteModalMode === 1){
      this.forumService.editForum(!this.course.courseDetails.forum.activated, this.course.courseDetails.id).subscribe(
        response => {
          console.log("Forum updated: active = " + response);
          //Only on succesful put we locally update the modified session
          this.course.courseDetails.forum.activated = response;
          this.allowForumEdition = false;
          this.updateCheckboxForumEdition(response);
          this.actions3.emit("closeModal");
        },
        error => console.log(error)
      );
    }
  }

  //DELETE existing Session
  deleteElement(){
    this.sessionService.deleteSession(this.updatedSession.id).subscribe(
      response => {
        console.log("Session deleted");
        console.log(response);
        //Only on succesful put we locally delete the session
        for (let i = 0; i < this.course.sessions.length; i++) {
          if (this.course.sessions[i].id == response.id) {
            this.course.sessions.splice(i, 1); //The session with the required ID is deleted
            this.updatedSession = this.course.sessions[0];
            break;
          }
        }
        this.actions3.emit("closeModal");
      },
      error => console.log(error)
    );
  }


//PRIVATE AUXILIAR METHODS

//Sorts an array of Session by their 'date' attribute (the first are the erliest)
  private sortSessionsByDate(sessionArray: Session[]): void {
    sessionArray.sort(function(a,b) {return (a.date > b.date) ? 1 : ((b.date > a.date) ? -1 : 0);} );
  }

  //Transforms a Date object into a single string ("HH:MM")
  private dateToTimeInputFormat(date:Date): string {
    let hours = date.getHours() < 10 ? "0" + date.getHours().toString() : date.getHours().toString();
    let minutes = date.getMinutes() < 10 ? "0" + date.getMinutes().toString() : date.getMinutes().toString();
    return(hours + ":" + minutes);
  }

  //Transforms two strings ("YYYY-MM-DD", "HH:MM") into a new Date object
  private fromInputToNumberDate(date: string, time: string): number {
    let newDate: Date = new Date(date); //date parameter has a valid ISO format: YYYY-MM-DD
    let timeArray = time.split(":");
    newDate.setHours(parseInt(timeArray[0]));
    newDate.setMinutes(parseInt(timeArray[1]));
    return newDate.getTime(); //returning miliseconds
  }

  //Returns the earliest Comment (by 'date' attribute) in the recursive structure of comments which has Comment 'c' as root
  recursiveReplyDateCheck(c: Comment): Comment{
    for (let r of c.replies){
      if (r.date > c.date) c = r;
      c = this.recursiveReplyDateCheck(r);
    }
    return c;
  }

}
