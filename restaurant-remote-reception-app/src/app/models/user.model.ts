export interface IUser {
  id: number;
  full_name: string;
  email: string;
}

export class UserSignup {
  constructor(
    public full_name: string,
    public email: string,
    public password: string
  ) {
  }
}

export class UserLogin {
  constructor(
    public email: string,
    public password: string
  ) {
  }
}
